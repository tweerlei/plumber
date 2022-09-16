package de.tweerlei.plumber.pipeline.options

class StringPipelineOption(
    override val name: String,
    override val description: String,
    private val defaultValue: String
): PipelineOption<String> {

    override fun argDescription() =
        defaultValue.ifEmpty { "<arg>" }

    override fun parse(value: String?) =
        value ?: defaultValue
}
