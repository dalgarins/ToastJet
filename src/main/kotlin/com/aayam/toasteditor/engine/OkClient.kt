package com.aayam.toasteditor.engine

import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.constants.interfaces.apis.ApiResponse
import com.aayam.toasteditor.engine.utils.*
import okhttp3.*
import java.net.CookieManager
import java.net.HttpCookie

object OkClient {
    private val cookieManager: CookieManager = CookieManagerStore.store

    val client: OkHttpClient = OkHttpClient.Builder()
        .cookieJar(object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val uri = url.toUri()
                cookies.forEach { cookie ->
                    println("Are we here at save cookie ${cookie.domain}")
                    val httpCookie = HttpCookie(cookie.name, cookie.value).apply {
                        domain = if (cookie.hostOnly) cookie.domain else ".${cookie.domain}"
                        path = cookie.path
                        secure = cookie.secure
                        maxAge = cookie.expiresAt / 1000 // Convert milliseconds to seconds
                        isHttpOnly = cookie.httpOnly
                    }
                    cookieManager.cookieStore.add(uri, httpCookie)
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                val uri = url.toUri()
                return cookieManager.cookieStore.get(uri).map { httpCookie ->
                    Cookie.Builder()
                        .name(httpCookie.name)
                        .value(httpCookie.value)
                        .path(httpCookie.path)
                        .apply {
                            if (httpCookie.secure) secure()
                            if (httpCookie.isHttpOnly) httpOnly()
                            if (httpCookie.domain.startsWith(".")) {
                                domain(httpCookie.domain.removePrefix("."))
                            } else {
                                hostOnlyDomain(httpCookie.domain)
                            }
                        }
                        .build()
                }
            }
        })
        .build()

    fun performRequest(api: ApiData, path: String): ApiResponse {
        val errorMessage = mutableListOf<String>()
        val warningMessage = mutableListOf<String>()

        try {
            var parsedUrl = handlePath(api.url, api.path)
            parsedUrl = handleParams(parsedUrl, api.params)
            val mimeType = getMimeType(parsedUrl)
            val reqHeaders = handleHeaders(api.headers)
            val requestBody = handleOkBodyType(api, path)
            val requestBuilder = Request.Builder()
                .url(parsedUrl)
                .method(api.method.name, requestBody)
            reqHeaders.forEach { (key, value) ->
                requestBuilder.addHeader(key, value)
            }
            val request = requestBuilder.build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    errorMessage.add("The request was not successfull")
                }

                val body = response.body.string()
                val respHeaders = response.headers.toMultimap().mapValues { it.value.joinToString(", ") }
                return ApiResponse(
                    invoked = true,
                    name = "",
                    saved = false,
                    //TODO: here correct this code so other works
                    error = true,
                    mime = mimeType,
                    parsedUrl = parsedUrl,
                    timeTaken = 0f,
                    data = body,
                    status = response.code,
                    statusText = response.message,
                    headers = respHeaders,
                    size = body.length.toFloat(),
                    cookie = handleCookies(respHeaders),
                    errorMessage = errorMessage,
                    warningMessage = warningMessage,
                    varUsed = emptyMap(),
                    tests = emptyList()
                )
            }
        } catch (e: Exception) {
            println("What is the exception $e")
            errorMessage.add("Unexpected error occurred: ${e.message}")
        }

        return ApiResponse(
            invoked = true,
            name = "",
            saved = false,
            error = true,
            mime = "",
            parsedUrl = api.url,
            timeTaken = 0f,
            data = "",
            status = 400,
            statusText = "Failed",
            headers = emptyMap(),
            size = 0f,
            cookie = emptyList(),
            errorMessage = errorMessage,
            warningMessage = warningMessage,
            varUsed = emptyMap(),
            tests = emptyList()
        )
    }
}
