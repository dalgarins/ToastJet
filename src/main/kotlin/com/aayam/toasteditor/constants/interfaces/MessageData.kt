package com.aayam.toasteditor.constants.interfaces

import com.aayam.toasteditor.constants.enums.MessageType
import kotlinx.serialization.Serializable

@Serializable
data class MessageData<T>(
    val type: MessageType,
    val data: T,
)