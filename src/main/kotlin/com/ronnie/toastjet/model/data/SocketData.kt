package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.SocketType
import java.util.Date

data class SocketMessage(
    var title:String,
    val message:String,
)

data class SocketRequestData(
    val url: String = "",
    val name: String = "",
    val headers: MutableList<KeyValueChecked> = mutableListOf(),
    val params: MutableList<KeyValueChecked> = mutableListOf(),
    val path: MutableList<KeyValue> = mutableListOf(),
    val invokedAt: Date? = null,
    val cookie: MutableList<CookieData> = mutableListOf(),
    val socketType: SocketType = SocketType.WebSocket,
    val id: String = "",
    val socketMessage : MutableList<SocketMessage> = mutableListOf()
)