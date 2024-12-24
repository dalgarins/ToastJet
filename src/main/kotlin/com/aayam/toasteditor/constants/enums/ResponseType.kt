package com.aayam.toasteditor.constants.enums

enum class ResponseType(val value:String) {
    Json("json"),
    Text("text"),
    Blob("blob"),
    ArrayBuffer("arrayBuffer"),
    Formdata("formdata"),
    Document("document"),
    Stream("stream")
}