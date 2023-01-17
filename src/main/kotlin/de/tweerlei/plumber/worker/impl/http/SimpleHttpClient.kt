package de.tweerlei.plumber.worker.impl.http

import de.tweerlei.plumber.worker.types.*
import java.net.HttpURLConnection
import java.net.URL
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class SimpleHttpClient {

    companion object {
        const val CONTENT_LENGTH = "Content-Length"
        const val CONTENT_TYPE = "Content-Type"
        const val STATUS = "Status"
        const val DATE = "Date"
        const val LAST_MODIFIED = "Last-Modified"
        const val EXPIRES = "Expires"

        private val ZONE_ID_GMT = ZoneId.of("GMT")

        fun interact(input: HttpEntity) =
            URL(input.url).openConnection()
                .let { connection ->
                    when (connection) {
                        is HttpURLConnection -> connection.interact(input)
                        else -> throw IllegalArgumentException("Not an HTTP URL")
                    }
                }

        private fun HttpURLConnection.interact(input: HttpEntity): HttpEntity =
            input
                .let {
                    it.headers.toAny().forEach { (key, value) ->
                        setRequestProperty(key, value.toHeaderField())
                    }
                    requestMethod = input.method
                    it.body
                }.also { bytes ->
                    if (bytes != null) {
                        doInput = true
                        setRequestProperty(CONTENT_LENGTH, bytes.size.toString())
                    }
                }.also {
                    // TODO: Set connect and read timeouts
                    connect()
                }?.also { bytes ->
                    outputStream.use { stream ->
                        stream.write(bytes)
                    }
                }.let {
                    inputStream.use { stream ->
                        stream.readAllBytes()
                    }
                }.let { bytes ->
                    if (!responseCode.isOK())
                        throw IllegalStateException("Server replied $responseCode $responseMessage")
                    HttpEntity(
                        input.method,
                        input.url,
                        bytes,
                        headerFields.toRecord(),
                        url.path.substringAfterLast("/"),
                        url.path.substringBeforeLast("/")
                    )
                }

        private fun Int.isOK() =
            this in 200..299

        private fun Map<String?, List<String>>.toRecord() =
            Record().also { rec ->
                forEach { (key, values) ->
                    when (key) {
                        // HTTP status code has no key
                        null -> rec.setValue(STATUS, values.first().toValue())
                        DATE, LAST_MODIFIED, EXPIRES ->
                            rec.setValue(key, values.first().toInstantValue())
                        CONTENT_LENGTH ->
                            rec.setValue(key, values.first().toComparableValue())
                        else -> rec.setValue(key, values.first().toValue())
                    }
                }
            }

        private fun String.toInstantValue() =
            try {
                ZonedDateTime.parse(toString(), DateTimeFormatter.RFC_1123_DATE_TIME)
                    .toInstant()
                    .let { instant -> InstantValue.of(instant) }
            } catch (e: DateTimeParseException) {
                // invalid date format
                NullValue.INSTANCE
            }

        private fun Value.toHeaderField() =
            when (this) {
                is InstantValue -> ZonedDateTime.ofInstant(toInstant(), ZONE_ID_GMT)
                    .format(DateTimeFormatter.RFC_1123_DATE_TIME)
                else -> toString()
            }
    }
}
