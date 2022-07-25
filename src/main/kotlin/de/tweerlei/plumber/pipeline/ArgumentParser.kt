package de.tweerlei.plumber.pipeline

import de.tweerlei.plumber.worker.toComparable

fun String.toWorkItemValue() =
    when {
        startsWith(":") -> substring(1)
        else -> toComparable()
    }
