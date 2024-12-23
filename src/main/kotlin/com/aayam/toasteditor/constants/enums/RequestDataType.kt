package com.aayam.toasteditor.constants.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = RequestDataTypeSerializer::class)
enum class RequestDataType(val value:Int) {
    FormData(0),
    RawJson(1),
    RawJs(2),
    RawXml(3),
    RawHtml(4),
    RawText(5),
    UrlEncoded(6),
    None(7),
    Binary(8),
}

object RequestDataTypeSerializer : KSerializer<RequestDataType> {
    override val descriptor = PrimitiveSerialDescriptor("RequestDataType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): RequestDataType {
        val code = decoder.decodeInt()
        return RequestDataType.values().first { it.value == code }
    }

    override fun serialize(encoder: Encoder, value: RequestDataType) {
        encoder.encodeInt(value.value)
    }
}