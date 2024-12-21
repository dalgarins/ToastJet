package com.aayam.toasteditor.constants.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

const val documentSeparator = "//*-----|****|-----*//"

@Serializable(with = MessageTypeSerializer::class)
enum class MessageType (val value: Int){
    Initialize(0),

    FilePicker(1),
    FileSaver(2),
    FileDelete(3),

    GetVariables(4),
    SaveVariables(5),
    LoadEnvironment(6),

    GetRawRequest(7),
    SaveRequest(8),
    GetResponse(9),
    GetResponseFromNonce(10),
}

object MessageTypeSerializer : KSerializer<MessageType> {
    override val descriptor = PrimitiveSerialDescriptor("MessageType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): MessageType {
        val code = decoder.decodeInt()
        return MessageType.values().first { it.value == code }
    }

    override fun serialize(encoder: Encoder, value: MessageType) {
        encoder.encodeInt(value.value)
    }
}
