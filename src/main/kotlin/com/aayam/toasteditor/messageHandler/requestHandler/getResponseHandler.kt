package com.aayam.toasteditor.messageHandler.requestHandler

import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.constants.interfaces.apis.ApiResponse
import com.aayam.toasteditor.engine.JavaClient
import com.aayam.toasteditor.engine.KtorHttpClient
import com.aayam.toasteditor.engine.OkClient
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json



fun getResponseHandler(data: ApiData, path: String): String? {
    return runBlocking { // Use runBlocking to wait for coroutine completion
//        try {
            var response = OkClient.performRequest(data,path)
            if (response.error) {
                response = JavaClient.performRequest(data,path)
                if (response.error) {
                    val ktorResponse = KtorHttpClient.performRequest(data,path)
                    return@runBlocking Json.encodeToString(ApiResponse.serializer(), ktorResponse)
                } else {
                    return@runBlocking Json.encodeToString(ApiResponse.serializer(), response)
                }
            } else {
                return@runBlocking Json.encodeToString(ApiResponse.serializer(), response)
            }
//        } catch (err: Exception) {
//            println("Error while getting the response: ${err.message}")
//            null // Return null in case of an error
//        }
    }
}
