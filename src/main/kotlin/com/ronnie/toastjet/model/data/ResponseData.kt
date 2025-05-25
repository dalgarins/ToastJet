package com.ronnie.toastjet.model.data

data class ResponseData(
    val apiRequestData: RequestData,
    val url: String,
    val name: String,
    val description: String,
    val requestHeaders: Map<String, String>,
    val responseHeaders: Map<String, String>,
    val error: Boolean,
    val errorMessage: List<String> = ArrayList(),
    val size: Int = 0,
    val setCookie: List<CookieData> = ArrayList(),
    val timeTaken: Long = 0L,
    val status: Int,
    val statusText: String,
    val data: String?
)
