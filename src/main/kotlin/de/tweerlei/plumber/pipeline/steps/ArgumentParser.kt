package de.tweerlei.plumber.pipeline.steps

import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.types.toComparable

fun String.toWorkItemAccessor(): WorkItemAccessor<Any?> =
    when {
        startsWith(":") -> { _ -> substring(1) }
        startsWith("@") -> { item -> item.getOptional(substring(1)) }
        else -> { _ -> toComparable() }
    }
