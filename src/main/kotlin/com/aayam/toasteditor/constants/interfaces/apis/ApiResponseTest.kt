package com.aayam.toasteditor.constants.interfaces.apis

import kotlinx.serialization.Serializable

@Serializable()
data class ApiResponseTest(
    val name:String,
    val status: Boolean,
    val message: String,
)
