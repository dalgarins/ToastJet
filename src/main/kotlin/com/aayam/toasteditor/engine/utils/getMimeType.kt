package com.aayam.toasteditor.engine.utils

import com.aayam.toasteditor.engine.JavaClient
import com.aayam.toasteditor.engine.OkClient
import okhttp3.Request
import java.net.URI
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun getMimeType(url: String): String {
    return try {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method("HEAD", HttpRequest.BodyPublishers.noBody())
            .build()

        val response = JavaClient.client.send(request, HttpResponse.BodyHandlers.discarding())
        response.headers().firstValue("content-type").orElse("")
    } catch (err: Exception) {
        println("HttpClient failed: ${err.message}")
        try {
            val request = okhttp3.Request.Builder()
                .url(url)
                .head()
                .build()

            OkClient.client.newCall(request).execute().use { response ->
                response.header("content-type") ?: ""
            }
        } catch (err: Exception) {
            println("OkHttp failed: ${err.message}")
            ""
        }
    }
}
