package com.ronnie.toastjet.engine.apiEngine.rest.client

import com.google.gson.Gson
import com.ronnie.toastjet.engine.apiEngine.rest.utils.CookieManagerStore
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleGraphQLHeaders
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleOkClientBody
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handlePath
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleRestHeaders
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.GraphQLRequestData
import com.ronnie.toastjet.model.data.Member
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.data.ResponseData
import com.ronnie.toastjet.model.data.SchemaInfo
import com.ronnie.toastjet.model.data.toCookieData
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.swing.store.configStore
import com.ronnie.toastjet.utils.apiUtils.extractCookies
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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

    fun fetchGraphQLSchema(graphqlRequest: GraphQLRequestData): MutableList<SchemaInfo> {
        val urlString = graphqlRequest.url
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            return mutableListOf()
        }

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
                    val requestCookies = graphqlRequest.cookie

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
        val introspectionQuery = """
        {
          __schema {
            types {
              name
              kind
              fields {
                name
                type {
                  name
                  kind
                }
              }
            }
          }
        }
    """.trimIndent()
        val requestHeaders = handleGraphQLHeaders(graphqlRequest)
        val requestBuilder = Request.Builder()
            .url(graphqlRequest.url) // This is the line that was throwing the error
        requestHeaders.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = HashMap<String, String>()
        requestBody.put("query", introspectionQuery)
        val gson = Gson()
        requestBuilder.post(gson.toJson(requestBody).toRequestBody(mediaType))

        val request = requestBuilder.build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("HTTP error ${response.code}: ${response.message}")
                val jsonBody = Json.parseToJsonElement(response.body!!.string()).jsonObject
                val types = jsonBody["data"]?.jsonObject
                    ?.get("__schema")?.jsonObject
                    ?.get("types")?.jsonArray ?: return mutableListOf()

                return types.mapNotNull { typeElement ->
                    val typeObj = typeElement.jsonObject
                    val name = typeObj["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val kind = typeObj["kind"]?.jsonPrimitive?.content ?: "UNKNOWN"
                    val fields = if (typeObj["fields"] is JsonArray) typeObj["fields"]?.jsonArray else emptyList()

                    val members = fields?.mapNotNull { field ->
                        val fieldObj = field.jsonObject
                        val fieldName = fieldObj["name"]?.jsonPrimitive?.content ?: return@mapNotNull null
                        val fieldType = fieldObj["type"]?.jsonObject
                        val typeName = fieldType?.get("name")?.jsonPrimitive?.contentOrNull
                            ?: fieldType?.get("kind")?.jsonPrimitive?.contentOrNull // Fallback to kind if name is null (e.g., for scalar types)
                            ?: run {
                                val ofType = fieldType?.get("ofType")?.jsonObject
                                ofType?.get("name")?.jsonPrimitive?.contentOrNull
                                    ?: ofType?.get("kind")?.jsonPrimitive?.contentOrNull
                                    ?: "Unknown" // Default if all else fails
                            }

                        Member(fieldName, typeName)
                    } ?: emptyList()
                    SchemaInfo(schemaName = name, schemaType = kind, members = members)
                }.toMutableList()
            }
        } catch (e: Exception) {
            println("Error fetching GraphQL schema: ${e.message}")
            throw e
        }
    }
}