package com.aayam.toasteditor.messageHandler.requestHandler

import com.aayam.toasteditor.cache.RequestCache
import com.google.gson.Gson

fun getRawRequestHandler(): String {
    val gSon = Gson()
    return gSon.toJson(RequestCache.apis)
}