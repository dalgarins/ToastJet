package com.aayam.toasteditor.engine.utils

import com.aayam.toasteditor.constants.interfaces.datas.KeyValueCheckRecord
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


fun handleParams(url: String, params: List<KeyValueCheckRecord>): String {
    val uri = URI(url)
    val queryParams = mutableListOf<String>()

    params.forEach { v ->
        if (v.enabled && v.key.trim().isNotEmpty() && v.value.trim().isNotEmpty()) {
            val encodedKey = URLEncoder.encode(v.key, StandardCharsets.UTF_8.toString())
            val encodedValue = URLEncoder.encode(v.value, StandardCharsets.UTF_8.toString())
            queryParams.add("$encodedKey=$encodedValue")
        }
    }

    val newQuery = queryParams.joinToString("&")
    return URI(uri.scheme, uri.authority, uri.path, newQuery, uri.fragment).toString()
}
