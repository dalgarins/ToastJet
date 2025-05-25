package com.ronnie.toastjet.engine.apiEngine.rest

import com.ronnie.toastjet.engine.apiEngine.rest.client.OkClient
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.data.ResponseData

fun invokeRestApi(request: RequestData): ResponseData {
    try {
        return OkClient.performRequest(request)
    } catch (e: Exception) {
        println("The exception is $e")
        throw e
    }
}