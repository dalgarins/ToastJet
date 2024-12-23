package com.aayam.toasteditor.constants.interfaces.datas

import kotlinx.serialization.Serializable

@Serializable
data class KeyValueCheckRecord(
    val key: String,
    val value: String,
    val enabled: Boolean
)
