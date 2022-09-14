package de.tweerlei.plumber.pipeline.options

interface PipelineOption<T> {

    val name: String
    val description: String
    fun argDescription(): String =
        ""

    fun parse(value: String?): T
}
