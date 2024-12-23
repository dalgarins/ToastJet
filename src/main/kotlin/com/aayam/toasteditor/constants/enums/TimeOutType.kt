package com.aayam.toasteditor.constants.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType

@Serializable(with= TimeOutTypeSerializer::class)
enum class TimeOutType(val value: Int) {
    ms(0),
    s(1),
    mins(2)
}

object TimeOutTypeSerializer : KSerializer<TimeOutType> {
    override val descriptor = PrimitiveSerialDescriptor("TimeOutType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): TimeOutType {
        val code = decoder.decodeInt()
        return TimeOutType.values().first { it.value == code }
    }

    override fun serialize(encoder: Encoder, value: TimeOutType) {
        encoder.encodeInt(value.value)
    }
}