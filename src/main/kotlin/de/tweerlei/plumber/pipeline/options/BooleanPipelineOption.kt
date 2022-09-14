package de.tweerlei.plumber.pipeline.options

class BooleanPipelineOption(
    override val name: String,
    override val description: String
): PipelineOption<Boolean> {

    override fun parse(value: String?) =
        value.toBoolean()
}
