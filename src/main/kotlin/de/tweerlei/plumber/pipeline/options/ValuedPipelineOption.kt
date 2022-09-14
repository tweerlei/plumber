package de.tweerlei.plumber.pipeline.options

class ValuedPipelineOption<T>(
    override val name: String,
    override val description: String,
    private val converter: (String?) -> T
): PipelineOption<T> {

    override fun argDescription() =
        converter(null)?.toString().let { arg ->
            if (arg.isNullOrEmpty()) "<arg>"
            else arg
        }

    override fun parse(value: String?) =
        converter(value)
}
