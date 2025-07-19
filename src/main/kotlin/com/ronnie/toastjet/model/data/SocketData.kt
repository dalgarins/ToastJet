package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.SocketType
import java.util.Date

data class SocketMessage(
    var title:String,
    var message:String,
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

data class SocketMessageData(
    val message: String,
    val time:Date,
    val send:Boolean,
    val event : String? = null
)

class SocketResponseData(
    override var isBeingInvoked: Boolean = false,
    override var invoked: Boolean = false,
    override val invokedAt: Date = Date(),
    override val url: String = "",
    override val name: String = "",
    override val description: String = "",
    override val requestHeaders: Map<String, String> = HashMap(),
    override val responseHeaders: Map<String, String> = emptyMap(),
    override val error: Boolean = false,
    override val errorMessage: List<String> = emptyList(),
    override val size: Int = 0,
    override val setCookie: List<CookieData> = emptyList(),
    override val timeTaken: Long = 0L,
    override val status: Int = 200,
    override val statusText: String = "OK",
    override val data: String? = null,
    override val tests: MutableList<ResponseTest> = mutableListOf()
) : ResponseData