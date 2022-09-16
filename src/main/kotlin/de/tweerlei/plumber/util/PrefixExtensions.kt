package de.tweerlei.plumber.util

fun extractCommonPrefix(startAfter: String, endWith: String) =
    StringBuilder().also { commonPrefix ->
        val startLength = startAfter.length
        val endLength = endWith.length
        var index = 0
        while (index < startLength && index < endLength && startAfter[index] == endWith[index]) {
            commonPrefix.append(startAfter[index])
            index++
        }
    }.toString()
