package com.ronnie.toastjet.engine.apiEngine.rest.client

import com.ronnie.toastjet.engine.apiEngine.getOkClient
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleOkClientBody
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handlePath
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleRestHeaders
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.data.RestResponseData
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.swing.store.configStore
import com.ronnie.toastjet.utils.apiUtils.extractCookies
import io.ktor.http.HttpStatusCode
import okhttp3.Request
import java.time.Duration
import java.time.LocalDateTime
import kotlin.collections.forEach

object OkClient {

    fun performRequest(apiRequest: RequestData): RestResponseData {

        val client = getOkClient(configStore!!, apiRequest.cookie)

        val requestUrl = handlePath(apiRequest.url, configStore?.state?.getState()?.baseDomain, apiRequest.path)
        val requestHeaders = handleRestHeaders(apiRequest)
        val requestBuilder = Request.Builder()
            .url(requestUrl)
        val requestBody = if (apiRequest.method in HttpMethod.GET_TYPE_METHODS) {
            handleOkClientBody(apiRequest)
        } else {
            null
        }
        requestBuilder.method(apiRequest.method.name, requestBody)

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
                return RestResponseData(
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
        return RestResponseData(
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