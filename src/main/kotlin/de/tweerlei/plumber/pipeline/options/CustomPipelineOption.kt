package de.tweerlei.plumber.pipeline.options

class CustomPipelineOption<T>(
    override val name: String,
    override val description: String,
    private val argDescription: String? = null,
    private val converter: (String?) -> T
): PipelineOption<T> {

    override fun argDescription() =
        argDescription ?:
        converter(null)?.toString().let { arg ->
            if (arg.isNullOrEmpty()) "<arg>"
            else arg
        }

    override fun parse(value: String?) =
        converter(value)
}
