package com.aayam.toasteditor.engine

import com.aayam.toasteditor.constants.enums.TimeOutType
import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.constants.interfaces.apis.ApiResponse
import com.aayam.toasteditor.engine.utils.*
import okio.utf8Size
import java.net.URI
import java.net.http.*
import java.time.Duration

object JavaClient {

    val client: HttpClient = HttpClient
        .newBuilder()
        .cookieHandler(CookieManagerStore.store)
        .build()

    fun performRequest(api: ApiData, path: String): ApiResponse {
        val errorMessage = mutableListOf<String>()
        val warningMessage = mutableListOf<String>()

        try {
            var parsedUrl = handlePath(api.url, api.path)
            parsedUrl = handleParams(parsedUrl, api.params)

            val mimeType = getMimeType(parsedUrl)
            val responsePriority = getResponseTypePriority(mimeType)

            val startTime = System.currentTimeMillis()

            val headers = handleHeaders(api.headers)

            val requestBuilder = HttpRequest.newBuilder()
                .method(api.method.name, handleJavaBodyData(api, path))
                .uri(URI.create(parsedUrl))

            if (api.timeout > 0) {
                if (api.timeoutType == TimeOutType.s) {
                    requestBuilder.timeout(Duration.ofSeconds(api.timeout.toLong()))
                } else if (api.timeoutType == TimeOutType.ms) {
                    requestBuilder.timeout(Duration.ofMillis(api.timeout.toLong()))
                } else {
                    requestBuilder.timeout(Duration.ofMinutes(api.timeout.toLong()))
                }
            }



            headers.entries.forEach {
                requestBuilder.setHeader(it.key, it.value)
            }


            val request = requestBuilder
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val endTime = System.currentTimeMillis()
            val timeTaken = endTime - startTime
            val body = response.body()
            val respHeaders = response.headers().map().mapValues {
                it.value.joinToString(",")
            }
            return ApiResponse(
                invoked = true,
                name = "",
                saved = false,
                error = true,
                mime = mimeType,
                parsedUrl = parsedUrl,
                timeTaken = timeTaken.toFloat(),
                data = body,
                status = response.statusCode(),
                statusText = response.statusCode().toString(),
                headers = respHeaders,
                size = body.utf8Size().toFloat(),
                cookie = handleCookies(respHeaders),
                errorMessage = emptyList(),
                warningMessage = emptyList(),
                varUsed = emptyMap(),
                tests = emptyList()
            )
        } catch (e: Exception) {
            println(e)
            when (e) {
                is HttpTimeoutException -> errorMessage.add("Request timed out: ${e.message}")
                is HttpConnectTimeoutException -> errorMessage.add("Connection timed out: ${e.message}")
                else -> errorMessage.add("Unexpected error occurred: ${e.message}")
            }
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
