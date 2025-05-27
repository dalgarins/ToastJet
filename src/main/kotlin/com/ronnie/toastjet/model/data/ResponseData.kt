package com.ronnie.toastjet.model.data

import java.util.Date

data class ResponseData(
    var isBeingInvoked: Boolean = false,
    var invoked : Boolean = false,
    val apiRequestData: RequestData = RequestData(),
    val invokedAt : Date = Date(),
    val url: String = "",
    val name: String = "",
    val description: String = "",
    val requestHeaders: Map<String, String> = HashMap(),
    val responseHeaders: Map<String, String> = HashMap(),
    val error: Boolean = false,
    val errorMessage: List<String> = ArrayList(),
    val size: Int = 0,
    val setCookie: List<CookieData> = ArrayList(),
    val timeTaken: Long = 0L,
    val status: Int = 0,
    val statusText: String = "OK",
    val data: String? = null,
)
