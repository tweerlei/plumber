package de.tweerlei.plumber.pipeline.steps

import de.tweerlei.plumber.worker.types.toComparable

fun String.toWorkItemValue() =
    when {
        startsWith(":") -> substring(1)
        else -> toComparable()
    }
