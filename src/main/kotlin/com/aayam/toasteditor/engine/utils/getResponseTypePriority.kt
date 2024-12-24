package com.aayam.toasteditor.engine.utils

import com.aayam.toasteditor.constants.enums.ResponseType

fun getResponseTypePriority(mimeType:String):List<ResponseType>{
    val priority = mutableSetOf<ResponseType>()
    if(mimeType.contains("application/json")){
        priority.add(ResponseType.Json)
    }
    if(mimeType.contains("text/")){
        priority.add(ResponseType.Text)
    }
    if(mimeType.contains("application/octet-stream") || mimeType.contains("audio/") || mimeType.contains("video/")){
        priority.add(ResponseType.Blob)
    }
    if(mimeType.contains("multipart/form-data")){
        priority.add(ResponseType.Formdata)
    }
    if(mimeType.contains("application/stream")){
        priority.add(ResponseType.Stream)
    }
    if(mimeType.contains("application/x-www-form-urlencoded")){
        priority.add(ResponseType.Formdata)
    }
    else{
        priority.add(ResponseType.ArrayBuffer)
    }

    ResponseType.values().forEach {
        priority.add(it)
    }

    return priority.toList()
}