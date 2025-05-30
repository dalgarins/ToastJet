package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.CookieSameSite
import java.net.HttpCookie
import java.util.Date

data class CookieData(
    var enabled: Boolean = true,
    var key: String = "",
    var value: String = "",
    var httpOnly: Boolean = false,
    var domain: String = "",
    var path: String = "/",
    var secure: Boolean = true,
    var sameSite: CookieSameSite = CookieSameSite.None,
    var hostOnly: Boolean = false,
    var pathIsDefault: Boolean = true,
    var creationTime: Date = Date(),
    var expiryTime: Date = Date(),
)

fun HttpCookie.toCookieData(): CookieData {
    return CookieData(
        enabled = true,
        key = this.name,
        value = this.value,
        httpOnly = this.isHttpOnly,
        domain = this.domain ?: "",
        path = this.path ?: "/",
        secure = this.secure,
        hostOnly = this.domain?.startsWith(".") == false, // Simplified logic
        pathIsDefault = this.path == null || this.path == "/",
        creationTime = Date(),
        sameSite = CookieSameSite.valueOf(this.comment),
        expiryTime = this.maxAge.takeIf { it > 0 }?.let { Date(System.currentTimeMillis() + it * 1000) }
            ?: Date(Long.MAX_VALUE)
    )
}

fun CookieData.toHttpCookie(): HttpCookie {
    val cookie = HttpCookie(key, value).apply {
        domain = this@toHttpCookie.domain
        path = this@toHttpCookie.path
        secure = this@toHttpCookie.secure
        isHttpOnly = this@toHttpCookie.httpOnly
        maxAge = (expiryTime.time - System.currentTimeMillis()) / 1000
        comment = sameSite.value
        creationTime = this@toHttpCookie.creationTime
    }

    return cookie
}

