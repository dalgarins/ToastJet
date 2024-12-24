package com.aayam.toasteditor.engine.utils

fun handlePath(url: String, path: Map<String, String>): String {
    var parsedUrl = url
    path.forEach { (key, value) ->
        parsedUrl = parsedUrl.replace("#$key", value)
    }
    return parsedUrl
}
