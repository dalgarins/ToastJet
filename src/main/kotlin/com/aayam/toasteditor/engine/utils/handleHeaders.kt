package com.aayam.toasteditor.engine.utils

import com.aayam.toasteditor.constants.interfaces.datas.KeyValueCheckRecord

fun handleHeaders(headers: List<KeyValueCheckRecord>): Map<String, String> {
    val validHeaders = mutableMapOf<String, String>()
    for (x in headers) {
        if (x.enabled && x.key.trim() != "" && x.value.trim() != "") {
            validHeaders[x.key] = x.value
        }
    }
    return validHeaders
}
