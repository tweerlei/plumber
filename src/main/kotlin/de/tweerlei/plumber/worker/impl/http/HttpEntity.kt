package de.tweerlei.plumber.worker.impl.http

import de.tweerlei.plumber.worker.WorkItem
import de.tweerlei.plumber.worker.impl.WellKnownKeys
import de.tweerlei.plumber.worker.types.LongValue
import de.tweerlei.plumber.worker.types.Record
import de.tweerlei.plumber.worker.types.StringValue
import de.tweerlei.plumber.worker.types.toValue

data class HttpEntity(
    val method: String,
    val url: String,
    val body: ByteArray?,
    val headers: Record,
    val name: String? = null,
    val path: String? = null
) {

    companion object {
        fun from(method: String, url: String, item: WorkItem) =
            HttpEntity(
                method,
                url,
                item.get().toByteArray(),
                (item.getOptional(HttpKeys.HEADERS) ?: Record())
                    .toRecord()
            )

        fun fromEmpty(method: String, url: String, item: WorkItem) =
            HttpEntity(
                method,
                url,
                null,
                (item.getOptional(HttpKeys.HEADERS) ?: Record())
                    .toRecord()
            )
    }

    fun applyTo(item: WorkItem) {
        item.set(StringValue.of(method), HttpKeys.METHOD)
        item.set(StringValue.of(url), HttpKeys.URL)
        body.toValue().let { body ->
            item.set(body)
            item.set(LongValue.of(body.size()), WellKnownKeys.SIZE)
        }
        item.set(headers, HttpKeys.HEADERS)
        headers.getValue(SimpleHttpClient.CONTENT_TYPE)
            .also { contentType -> item.set(contentType, WellKnownKeys.CONTENT_TYPE) }
        headers.getValue(SimpleHttpClient.LAST_MODIFIED)
            .also { lastModified -> item.set(lastModified, WellKnownKeys.LAST_MODIFIED) }
        name?.let { item.set(StringValue.of(it), WellKnownKeys.NAME) }
        path?.let { item.set(StringValue.of(it), WellKnownKeys.PATH) }
    }
}
