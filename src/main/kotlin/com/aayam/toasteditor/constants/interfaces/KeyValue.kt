package com.aayam.toasteditor.constants.interfaces

import kotlinx.serialization.Serializable

@Serializable
data class KeyValue(
    val key: String,
    val value: String
)
