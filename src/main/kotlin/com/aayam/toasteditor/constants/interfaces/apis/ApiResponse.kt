package com.aayam.toasteditor.constants.interfaces.apis

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse(
    val invoked: Boolean ,
    val name: String,
    val saved: Boolean,
    val error: Boolean,
    val mime: String,
    val parsedUrl: String,
    val timeTaken: Float,
    val data: String?,
    val status: Int,
    val statusText: String,
    val headers: Map<String,String>,
    val size: Float,
    val cookie: Cookies,
    val errorMessage: String,
    val warningMessage: String,
    val varUsed: Map<String, String>,
    val tests: List<ApiResponseTest>
)
