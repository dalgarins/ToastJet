package com.aayam.toasteditor.engine

import com.aayam.toasteditor.constants.interfaces.apis.Cookies
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.HttpCookie
import java.net.URI


object CookieManagerStore {
    val store = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    fun populateCookies(cookies: List<Cookies>) {
        cookies.forEach { cookie ->
            val httpCookie = HttpCookie(cookie.key, cookie.value).apply {
                domain = cookie.domain ?: ""
                path = cookie.path ?: "/"
                secure = cookie.secure
                isHttpOnly = cookie.httpOnly
                maxAge = cookie.maxAge?.toLong() ?: Long.MAX_VALUE
            }

            // If hostOnly is true, do not explicitly set the domain
            if (cookie.hostOnly == true) {
                httpCookie.domain = null
            }

            val uri = URI("http://${cookie.domain ?: "localhost"}")
            this.store.cookieStore.add(uri, httpCookie)
        }
    }
}