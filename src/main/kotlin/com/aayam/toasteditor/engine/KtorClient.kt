package com.aayam.toasteditor.engine

import com.aayam.toasteditor.constants.enums.FormDataItem
import com.aayam.toasteditor.constants.enums.RequestDataType
import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.constants.interfaces.apis.ApiResponse
import com.aayam.toasteditor.engine.utils.*
import com.aayam.toasteditor.utilities.fileUtility.getAbsolutePath
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.reflect.*
import java.io.File
import java.net.HttpCookie

object KtorHttpClient {
    val client = HttpClient {
        install(HttpCookies) {
            storage = object : CookiesStorage {
                override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
                    CookieManagerStore.store.cookieStore.add(
                        requestUrl.toURI(),
                        HttpCookie(cookie.name, cookie.value).apply {
                            domain = cookie.domain
                            path = cookie.path
                            secure = cookie.secure
                            isHttpOnly = cookie.httpOnly
                            maxAge = cookie.maxAge.toLong()
                        }
                    )
                }

                override fun close() {}

                override suspend fun get(requestUrl: Url): List<Cookie> {
                    return CookieManagerStore.store.cookieStore.get(requestUrl.toURI()).map {
                        Cookie(
                            name = it.name,
                            value = it.value,
                            maxAge = it.maxAge.toInt(),
                            domain = it.domain,
                            path = it.path,
                            secure = it.secure,
                            httpOnly = it.isHttpOnly,
                        )
                    }
                }

            }
        }
    }

    suspend fun performRequest(api: ApiData,path:String):ApiResponse{
        val errorMessage = mutableListOf<String>()
        val warningMessage = mutableListOf<String>()

        try {
            var parsedUrl = handlePath(api.url, api.path)
            parsedUrl = handleParams(parsedUrl, api.params)
            val mimeType = getMimeType(parsedUrl)
            val reqHeaders = handleHeaders(api.headers)
            if(api.requestDataType == RequestDataType.FormData){
                val response = client.submitFormWithBinaryData(
                    url = parsedUrl,
                    formData = formData {
                        api.formData.forEach {
                            if(it.type == FormDataItem.File){
                                val absolutePath = getAbsolutePath(path,it.value)
                                val file = File(absolutePath)
                                if(file.exists()) {
                                    append(it.key,file.readBytes(),Headers.build {
                                        append(HttpHeaders.ContentDisposition,"filename=${file.name}")
                                    })
                                }
                            }else{
                                append(it.key,it.value)
                            }
                        }
                    },
                    block = {
                        headers{
                            reqHeaders.forEach {
                                append(it.key,it.value)
                            }
                        }
                        timeout {
                            api.timeout
                        }
                    }
                )
            }
            if(api.requestDataType == RequestDataType.UrlEncoded){
                val response = client.request {
                    url {
                        path(parsedUrl)
                        contentType(ContentType.Application.FormUrlEncoded)
                        encodedPath = parsedUrl
                    }
                    method = HttpMethod.parse(api.method.name)
                    headers{
                        reqHeaders.forEach {
                            append(it.key,it.value)
                        }
                    }
                    timeout {
                        api.timeout
                    }
                    setBody(FormDataContent(
                        formData = Parameters.build {
                            api.urlEncoded.forEach {
                                if(it.enabled && (it.key!= "" || it.value != "")){
                                    append(it.key,it.value)
                                }
                            }
                        }
                    ))
                }
            }else{
                val body = when(api.requestDataType){
                    RequestDataType.RawJson -> api.json
                    RequestDataType.RawJs -> api.js
                    RequestDataType.RawXml -> api.xml
                    RequestDataType.RawHtml -> api.html
                    RequestDataType.RawText -> api.text
                    else -> null
                }
                val contentType = when(api.requestDataType){
                    RequestDataType.RawXml -> ContentType.Application.Xml
                    RequestDataType.RawJson -> ContentType.Application.Json
                    RequestDataType.RawJs -> ContentType.Application.JavaScript
                    RequestDataType.RawHtml -> ContentType.Text.Html
                    RequestDataType.RawText -> ContentType.Text.Plain
                    else -> ContentType.Any
                }
                val response = client.request {
                    url {
                        path(parsedUrl)
                        contentType(contentType)
                    }
                    method = HttpMethod.parse(api.method.name)
                    headers{
                        reqHeaders.forEach {
                            append(it.key,it.value)
                        }
                    }
                    timeout {
                        api.timeout
                    }
                    setBody(body)
                }
            }
        } catch (e: Exception) {
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