package com.aayam.toasteditor.constants.serializable

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


object NumberSerializer : KSerializer<Number?> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Number", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Number?) {
        when (value) {
            null -> encoder.encodeString("null")
            Double.POSITIVE_INFINITY -> encoder.encodeString("Infinity")
            Double.NEGATIVE_INFINITY -> encoder.encodeString("-Infinity")
            is Int -> encoder.encodeInt(value)
            is Long -> encoder.encodeLong(value)
            is Double -> encoder.encodeDouble(value)
            else -> throw IllegalArgumentException("Unsupported number type: ${value::class}")
        }
    }

    override fun deserialize(decoder: Decoder): Number? {
        val input = decoder.decodeString()
        return when (input) {
            "null" -> null
            "Infinity" -> Double.POSITIVE_INFINITY
            "-Infinity" -> Double.NEGATIVE_INFINITY
            else -> try {
                when {
                    input.contains(".") -> input.toDouble()
                    else -> input.toLong()
                }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid number format: $input")
            }
        }
    }
}
