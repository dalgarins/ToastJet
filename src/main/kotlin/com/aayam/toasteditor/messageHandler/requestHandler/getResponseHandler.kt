package com.aayam.toasteditor.messageHandler.requestHandler

import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.constants.interfaces.apis.ApiResponse
import com.aayam.toasteditor.engine.JavaClient
import com.aayam.toasteditor.engine.KtorHttpClient
import com.aayam.toasteditor.engine.OkClient
import com.aayam.toasteditor.utilities.fileUtility.findTosResponse
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.io.path.Path


fun getResponseHandler(data: ApiData, path: String): String? {

    fun saveResponseToFile(resp:String) {
        println("Are we saving the file")
        val tosDirectory = findTosResponse(path)
        val p = Path(tosDirectory,"${data.nonce}.json").toString()
        File(p).writeText(resp)
    }


    return runBlocking {
        try {
            var response = OkClient.performRequest(data, path)
            if (response.error) {
                response = JavaClient.performRequest(data, path)
                if (response.error) {
                    val ktorResponse = KtorHttpClient.performRequest(data, path)
                    val a =  Json.encodeToString(ApiResponse.serializer(), ktorResponse)
                    saveResponseToFile(a)
                    return@runBlocking a
                } else {
                    val a = Json.encodeToString(ApiResponse.serializer(), response)
                    saveResponseToFile(a)
                    return@runBlocking a
                }
            } else {
                val a = Json.encodeToString(ApiResponse.serializer(), response)
                saveResponseToFile(a)
                return@runBlocking a
            }
        } catch (err: Exception) {
            println("Error while getting the response: ${err.message}")
            null
        }
    }
}
