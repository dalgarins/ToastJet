package com.ronnie.toastjet.engine.apiEngine.rest.utils

import com.ronnie.toastjet.model.data.KeyValue
import java.net.URLEncoder

fun handlePath(url: String, baseDomain: String?, pathVars: List<KeyValue>): String {
    var refactoredUrl: String = url
    if (!url.startsWith("http") && baseDomain != null) {
        val baseDomainEndsWith = baseDomain.endsWith("/")
        val urlStartsWith = url.startsWith("/")
        refactoredUrl = if (baseDomainEndsWith && urlStartsWith) {
            baseDomain.removeSuffix("/") + url
        } else if (baseDomainEndsWith || urlStartsWith) {
            baseDomain + url
        } else {
            "$baseDomain/$url"
        }
    }
    for (pathVar in pathVars) {
        refactoredUrl = refactoredUrl.replace("{${pathVar.key}}", pathVar.value)
    }
    var normalizeUrl = normalizeUrl(refactoredUrl)
    while (normalizeUrl.endsWith("/") || normalizeUrl.endsWith("?")) {
        normalizeUrl = normalizeUrl.substring(0, normalizeUrl.length - 2)
    }
    return normalizeUrl
}


fun normalizeUrl(urlString: String): String {
    val urlSchemaSplit = urlString.split("//", limit = 2)
    val protocol = urlSchemaSplit[0]
    val urlWithoutSchema = urlSchemaSplit[1].replace(Regex("/+"), "/")
    val urlPathSearchSplit = urlWithoutSchema.split("?", limit = 2)
    val urlPath = urlPathSearchSplit[0]
    val searchPart = urlPathSearchSplit.getOrNull(1) ?: ""
    val hostPathSplit = urlPath.split("/", limit = 1)
    val host = hostPathSplit[0]
    val path = hostPathSplit.getOrNull(1) ?: ""


    val encodedPath = path.split('/')
        .joinToString("/") { segment -> URLEncoder.encode(segment, "UTF-8") }

    val encodedQuery = searchPart.takeIf { it.isNotEmpty() }?.let {
        it.split('&')
            .joinToString("&") { param ->
                val (key, value) = param.split('=', limit = 2)
                "${URLEncoder.encode(key, "UTF-8")}=${URLEncoder.encode(value, "UTF-8")}"
            }
    } ?: ""
    return "$protocol//$host/$encodedPath?$encodedQuery"
}
