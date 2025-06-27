package com.ronnie.toastjet.engine.apiEngine

import com.ronnie.toastjet.engine.apiEngine.rest.utils.CookieManagerStore
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.toCookieData
import com.ronnie.toastjet.swing.store.ConfigStore
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import java.util.Date

fun getOkClient(configStore: ConfigStore,requestCookies:List<CookieData>) : OkHttpClient {
    val client = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val cookieData = cookies.map { cookie ->
                    CookieData(
                        key = cookie.name,
                        value = cookie.value,
                        hostOnly = cookie.hostOnly,
                        domain = cookie.domain.removePrefix("www."),
                        path = cookie.path,
                        secure = cookie.secure,
                        httpOnly = cookie.httpOnly,
                        pathIsDefault = true,
                        creationTime = Date(),
                        expiryTime = Date(cookie.expiresAt)
                    )
                }
                configStore.state.setState {
                    it.cookie.addAll(cookieData)
                    CookieManagerStore.populateCookies(it.cookie)
                    it.cookie = CookieManagerStore.store.cookieStore.cookies
                        .map { it.toCookieData() }
                        .toMutableList()
                    it
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val cookies = configStore.state.getState().cookie

                return (cookies + requestCookies).filter { it.domain == url.host }.map {
                    Cookie.Builder()
                        .name(it.key)
                        .value(it.value)
                        .path(it.path)
                        .apply {
                            if (it.secure) secure()
                            if (it.httpOnly) httpOnly()
                            if (it.domain.startsWith(".")) {
                                domain(it.domain.removePrefix("."))
                            } else {
                                hostOnlyDomain(it.domain)
                            }
                        }
                        .expiresAt(it.expiryTime.time)
                        .build()
                }
            }
        })
        .build()

    return client
}