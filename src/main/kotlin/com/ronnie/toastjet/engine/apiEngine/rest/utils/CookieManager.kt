package com.ronnie.toastjet.engine.apiEngine.rest.utils

import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.toHttpCookie
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.URI


object CookieManagerStore {
    val store = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }
    fun populateCookies(cookies: List<CookieData>) {
        cookies.forEach { cookie ->
            val uri = URI("http://${cookie.domain}")
            this.store.cookieStore.add(uri, cookie.toHttpCookie())
        }
    }
}