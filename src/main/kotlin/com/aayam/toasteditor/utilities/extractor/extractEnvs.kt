package com.aayam.toasteditor.utilities.extractor

import com.aayam.toasteditor.constants.interfaces.KeyValue

fun extractEnvs(text: String): List<KeyValue> {
    val data: List<KeyValue?> = text.split("\n").map { line ->
        val arr = line.trim().split("=").map {
            it.trim()
        }
        if (arr.size == 2) {
            KeyValue(
                key = arr[0],
                value = arr[1]
            )
        } else {
            null
        }
    }
    return data.filterNotNull()
}