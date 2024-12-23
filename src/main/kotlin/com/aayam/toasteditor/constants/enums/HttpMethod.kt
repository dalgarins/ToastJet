package com.aayam.toasteditor.constants.enums

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with= HttpMethodSerializer::class)
enum class HttpMethod(val value: Int) {
    GET(0),
    POST(1),
    PUT(2),
    PATCH(3),
    DELETE(4),
    HEAD(5),
    OPTIONS(6),
    CONNECT(7),
    TRACE(8),
    PURGE(9),
    LINK(10),
    UNLINK(11),
    PROPFIND(12),
    PROPPATCH(13),
    MKCOL(14),
    COPY(15),
    MOVE(16),
    LOCK(17),
    UNLOCK(18),
    REPORT(19),
    VIEW(20)
}

object HttpMethodSerializer : KSerializer<HttpMethod> {
    override val descriptor = PrimitiveSerialDescriptor("FormDataItem", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): HttpMethod {
        val code = decoder.decodeInt()
        return HttpMethod.values().first { it.value == code }
    }

    override fun serialize(encoder: Encoder, value: HttpMethod) {
        encoder.encodeInt(value.value)
    }
}