package com.aayam.toasteditor.constants.interfaces.apis

import com.aayam.toasteditor.constants.serializable.NumberSerializer
import kotlinx.serialization.Serializable


@Serializable
data class Cookies(
    val key: String,
    val value: String,
    val expires: String?,
    val maxAge: Int?,
    val domain: String?,
    val path: String?,
    val secure: Boolean,
    val httpOnly: Boolean,
    val extensions: List<String>?,
    val creation: String?,
    @Serializable(with = NumberSerializer::class)
    val creationIndex: Number?,
    val hostOnly: Boolean?,
    val pathIsDefault: Boolean?,
    val lastAccessed: String?,
    val sameSite: String?,
)