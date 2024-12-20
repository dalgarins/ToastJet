package com.aayam.toasteditor.constants.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = VariableDataTypeSerializer::class)
enum class VariableDataType(val value:Int) {
    String(0),
    Number(1),
    Boolean(2),
}

object VariableDataTypeSerializer : KSerializer<VariableDataType> {
    override val descriptor = PrimitiveSerialDescriptor("VariableDataType", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): VariableDataType {
        val code = decoder.decodeInt()
        return VariableDataType.values().first { it.value == code }
    }

    override fun serialize(encoder: Encoder, value: VariableDataType) {
        encoder.encodeInt(value.value)
    }
}