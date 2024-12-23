package com.aayam.toasteditor.constants.interfaces.apis

import com.aayam.toasteditor.constants.enums.FormDataItem
import kotlinx.serialization.Serializable

@Serializable
data class FormDataType(
    val key:String,
    val value:String,
    val enabled:Boolean,
    val type: FormDataItem
)
