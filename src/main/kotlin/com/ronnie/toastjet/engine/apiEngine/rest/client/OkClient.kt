package com.ronnie.toastjet.engine.apiEngine.rest.client

import com.ronnie.toastjet.engine.apiEngine.rest.utils.CookieManagerStore
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleHeaders
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleOkClientBody
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handlePath
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.data.ResponseData
import com.ronnie.toastjet.model.data.toCookieData
import com.ronnie.toastjet.swing.store.configStore
import com.ronnie.toastjet.utils.apiUtils.extractCookies
import io.ktor.http.HttpStatusCode
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date
import kotlin.collections.forEach
import kotlin.collections.map

object OkClient {

    fun performRequest(apiRequest: RequestData): ResponseData {
        val client = OkHttpClient.Builder()
            .cookieJar(object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    val cookieData = cookies.map { cookie ->
                        println("The domain is ${apiRequest.url} ${cookie.domain}")
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
                    configStore?.state?.setState {
                        it.cookie.addAll(cookieData)
                        CookieManagerStore.populateCookies(it.cookie)
                        it.cookie = CookieManagerStore.store.cookieStore.cookies
                            .map { it.toCookieData() }
                            .toMutableList()
                        it
                    }
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val cookies = configStore?.state?.getState()?.cookie
                    val requestCookies = apiRequest.cookie

                    return ((cookies ?: emptyList()) + requestCookies).filter { it.domain == url.host }.map {
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

        val requestUrl = handlePath(apiRequest.url, configStore?.state?.getState()?.baseDomain, apiRequest.path)
        val requestHeaders = handleHeaders(apiRequest)
        val requestBody = handleOkClientBody(apiRequest)
        val requestBuilder = Request.Builder()
            .url(requestUrl)
            .method(apiRequest.method.name, requestBody)
        requestHeaders.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }
        val request = requestBuilder.build()
        try {
            val startTime = LocalDateTime.now()
            client.newCall(request).execute().use { response ->
                val endTime = LocalDateTime.now()
                val body = response.body?.string()
                val respHeaders = response.headers.toMultimap().mapValues { it.value.joinToString(", ") }
                return ResponseData(
                    invoked = true,
                    isBeingInvoked = false,
                    apiRequestData = apiRequest,
                    url = requestUrl,
                    name = "",
                    description = "",
                    requestHeaders = requestHeaders,
                    responseHeaders = respHeaders,
                    errorMessage = ArrayList(),
                    size = body?.length ?: 0,
                    status = response.code,
                    statusText = HttpStatusCode.fromValue(response.code).description,
                    data = body,
                    error = false,
                    setCookie = extractCookies(respHeaders),
                    timeTaken = Duration.between(startTime, endTime).toMillis()
                )
            }
        } catch (e: Exception) {
            println("What is the exception ")
            e.printStackTrace()
        }
        return ResponseData(
            invoked = true,
            isBeingInvoked = false,
            apiRequestData = apiRequest,
            url = requestUrl,
            name = "",
            description = "",
            requestHeaders = requestHeaders,
            responseHeaders = HashMap(),
            errorMessage = ArrayList(),
            size = 0,
            status = 400,
            statusText = "Failed",
            data = null,
            error = true,
            setCookie = ArrayList(),
            timeTaken = 0
        )
    }
}