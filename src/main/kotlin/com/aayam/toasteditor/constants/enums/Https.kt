package com.aayam.toasteditor.constants.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with= HttpsSerializer::class)
enum class Https(val value:Int) {
    Http1(0),
    Http2(1),
}

object HttpsSerializer : KSerializer<Https>{
    override val descriptor = PrimitiveSerialDescriptor("FormDataItem", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Https {
        val code = decoder.decodeInt()
        return Https.values().first { it.value == code }
    }

    override fun serialize(encoder: Encoder, value: Https) {
        encoder.encodeInt(value.value)
    }
}