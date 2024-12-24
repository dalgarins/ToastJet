package com.aayam.toasteditor.engine.utils

import com.aayam.toasteditor.constants.interfaces.apis.Cookies

fun handleCookies(headers: Map<String, Any>): List<Cookies> {
    val cookiesList = mutableListOf<Cookies>()
    headers["set-cookie"]?.let { setCookieHeader ->
        val cookieStrings = if (setCookieHeader is List<*>) {
            setCookieHeader.filterIsInstance<String>()
        } else {
            listOf(setCookieHeader as String)
        }

        cookieStrings.forEach { cookieStr ->
            parseCookie(cookieStr)?.let { cookie ->
                cookiesList.add(cookie)
            }
        }
    }
    return cookiesList
}

private fun parseCookie(cookieStr: String): Cookies? {
    val parts = cookieStr.split(";")
    val keyValue = parts[0].split("=", limit = 2)
    if (keyValue.size < 2) return null
    val key = keyValue[0].trim()
    val value = keyValue[1].trim()
    return Cookies(
        key = key,
        value = value,
        expires = null,
        maxAge = null,
        domain = null,
        path = null,
        secure = false,
        httpOnly = false,
        extensions = null,
        creation = null,
        creationIndex = null,
        hostOnly = null,
        pathIsDefault = null,
        lastAccessed = null,
        sameSite = null
    )
}