package com.ronnie.toastjet.model.data

import java.util.Date


interface ResponseData {
    val isBeingInvoked: Boolean
    val invoked: Boolean
    val invokedAt: Date
    val url: String
    val name: String
    val description: String
    val requestHeaders: Map<String, String>
    val responseHeaders: Map<String, String>
    val error: Boolean
    val errorMessage: List<String>
    val size: Int
    val setCookie: List<CookieData>
    val timeTaken: Long
    val status: Int
    val statusText: String
    val data: String?
    val tests: MutableList<ResponseTest>
}

class RestResponseData(
    val apiRequestData: RequestData = RequestData(),
    override var isBeingInvoked: Boolean = false,
    override var invoked: Boolean = false,
    override val invokedAt: Date = Date(),
    override val url: String = apiRequestData.url,
    override val name: String = apiRequestData.name,
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

data class ResponseTest(val name: String, val result: Boolean)
