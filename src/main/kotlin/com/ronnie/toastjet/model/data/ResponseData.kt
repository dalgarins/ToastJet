package com.ronnie.toastjet.model.data

import java.util.Date


open class ResponseData(
    open var isBeingInvoked: Boolean = false,
    open var invoked: Boolean = false,
    open val invokedAt: Date = Date(),
    open val url: String = "",
    open val name: String = "",
    open val description: String = "",
    open val requestHeaders: Map<String, String> = HashMap(),
    open val responseHeaders: Map<String, String> = HashMap(),
    open val error: Boolean = false,
    open val errorMessage: List<String> = ArrayList(),
    open val size: Int = 0,
    open val setCookie: List<CookieData> = ArrayList(),
    open val timeTaken: Long = 0L,
    open val status: Int = 0,
    open val statusText: String = "OK",
    open val data: String? = null,
    open val tests: MutableList<ResponseTest> = mutableListOf()
)

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
) : ResponseData()

data class ResponseTest(val name: String, val result: Boolean)
