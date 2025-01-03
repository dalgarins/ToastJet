package com.aayam.toasteditor.constants.interfaces

import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.constants.interfaces.apis.ApiResponse
import kotlinx.serialization.Serializable

@Serializable()
data class SaveExampleData(
    val name:String,
    val req: ApiData,
    val res:ApiResponse
)
