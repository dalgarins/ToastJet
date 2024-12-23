package com.aayam.toasteditor.constants.interfaces.apis

import com.aayam.toasteditor.constants.enums.HttpMethod
import com.aayam.toasteditor.constants.enums.Https
import com.aayam.toasteditor.constants.enums.RequestDataType
import com.aayam.toasteditor.constants.enums.TimeOutType
import com.aayam.toasteditor.constants.interfaces.datas.KeyValueCheckRecord
import kotlinx.serialization.Serializable

@Serializable
data class ApiData(
    val nonce: String,
    val rawCode: String,
    val url: String,
    val name: String,
    val method: HttpMethod,
    val https: Https,
    val params: List<KeyValueCheckRecord> = emptyList(),
    val headers: List<KeyValueCheckRecord> = emptyList(),
    val path: Map<String, String> = mapOf(),
    val requestCookies: List<Cookies> = emptyList(),
    val timeout: Float,
    val timeoutType: TimeOutType,
    val requestDataType: RequestDataType,
    val tests: List<ApiDataTest> = emptyList(),
    val json: String? = null,
    val xml: String? = null,
    val js: String? = null,
    val html: String? = null,
    val text: String? =  null,
    val formData: List<FormDataType> = emptyList(),
    val urlEncoded: List<KeyValueCheckRecord> = emptyList(),
    val binary: String? = null,
    val examples: List<ApiDataExample> = emptyList()
)
