package com.aayam.toasteditor.constants.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(FormDataItemSerializer::class)
enum class FormDataItem(val value: Int) {
    Text(0),
    Number(1),
    File(2),
    Boolean(3),
    Json(4),
    Xml(5),
}

object FormDataItemSerializer : KSerializer<FormDataItem> {
    override val descriptor = PrimitiveSerialDescriptor("FormDataItem", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): FormDataItem {
        val code = decoder.decodeInt()
        return FormDataItem.values().first { it.value == code }
    }

    override fun serialize(encoder: Encoder, value: FormDataItem) {
        encoder.encodeInt(value.value)
    }
}
