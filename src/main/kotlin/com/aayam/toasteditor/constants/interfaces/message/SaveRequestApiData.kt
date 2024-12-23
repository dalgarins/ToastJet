package com.aayam.toasteditor.constants.interfaces.message

import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import kotlinx.serialization.Serializable

@Serializable()
data class SaveRequestApiData(
    val index: Int,
    val api : ApiData
)
