package de.tweerlei.plumber.pipeline.steps

import de.tweerlei.plumber.worker.WorkItemAccessor
import de.tweerlei.plumber.worker.types.*

fun String.toWorkItemAccessor(): WorkItemAccessor<Value> =
    when {
        startsWith(":") -> { _ -> StringValue.of(substring(1)) }
        startsWith("@") -> { item -> item.get(substring(1)) }
        else -> { _ -> toComparableValue() }
    }

fun String.toWorkItemStringAccessor(): WorkItemAccessor<String> =
    when {
        startsWith(":") -> { _ -> substring(1) }
        startsWith("@") -> { item -> item.get(substring(1)).toString() }
        else -> { _ -> this }
    }

fun String.toRequiredAttributes(): Set<String> =
    when {
        startsWith("@") -> setOf(substring(1))
        else -> emptySet()
    }

fun String?.toOptionValue() =
    when {
        this == null -> NullValue.INSTANCE
        startsWith(":") -> StringValue.of(substring(1))
        else -> toComparableValue()
    }
