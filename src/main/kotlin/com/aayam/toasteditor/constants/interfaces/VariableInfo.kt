package com.aayam.toasteditor.constants.interfaces

import com.aayam.toasteditor.constants.enums.VariableDataType
import kotlinx.serialization.Serializable

@Serializable
data class VariableInfo(
    val key:String,
    val value:String,
    val enabled:Boolean,
    val type: VariableDataType
)
