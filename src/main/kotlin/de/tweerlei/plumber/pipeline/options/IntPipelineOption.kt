package de.tweerlei.plumber.pipeline.options

class IntPipelineOption(
    override val name: String,
    override val description: String,
    private val defaultValue: Int
): PipelineOption<Int> {

    override fun argDescription() =
        defaultValue.toString()

    override fun parse(value: String?) =
        value?.toInt() ?: defaultValue
}
