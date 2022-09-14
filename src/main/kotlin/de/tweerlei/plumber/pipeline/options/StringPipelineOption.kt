package de.tweerlei.plumber.pipeline.options

class StringPipelineOption(
    override val name: String,
    override val description: String
): PipelineOption<String?> {

    override fun argDescription() =
        "<arg>"

    override fun parse(value: String?) =
        value
}
