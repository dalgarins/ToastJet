package com.aayam.toasteditor.engine.utils

import com.aayam.toasteditor.constants.enums.FormDataItem
import com.aayam.toasteditor.constants.enums.RequestDataType
import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.utilities.fileUtility.getAbsolutePath
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLEncoder
import java.net.http.HttpRequest.BodyPublisher
import java.net.http.HttpRequest.BodyPublishers
import java.nio.charset.StandardCharsets
import java.nio.file.Files

fun handleJavaBodyData(api: ApiData, path: String): BodyPublisher {
    if (api.requestDataType == RequestDataType.RawJs) {
        return BodyPublishers.ofString(api.js)
    }
    if (api.requestDataType == RequestDataType.RawXml) {
        return BodyPublishers.ofString(api.xml)
    }
    if (api.requestDataType == RequestDataType.RawHtml) {
        return BodyPublishers.ofString(api.html)
    }
    if (api.requestDataType == RequestDataType.RawText) {
        return BodyPublishers.ofString(api.text)
    }
    if (api.requestDataType == RequestDataType.RawJson) {
        return BodyPublishers.ofString(api.json)
    }
    if (api.requestDataType == RequestDataType.FormData) {
        return handleFormData(api, path)
    }
    if (api.requestDataType == RequestDataType.UrlEncoded) {
        return handleUrlEncoded(api)
    }
    if (api.requestDataType == RequestDataType.Binary) {
        if(api.binary != null) {
            val absolutePath = getAbsolutePath(path, api.binary)
            val file = File(absolutePath)
            if(file.exists()){
                BodyPublishers.ofInputStream {
                    file.inputStream()
                }
            }
        }
        return BodyPublishers.noBody()
    }
    return BodyPublishers.noBody()
}

private fun handleFormData(api: ApiData, path: String): BodyPublisher {
    val boundary = "----WebKitFormBoundary" + System.currentTimeMillis()
    val builder = StringBuilder()

    api.formData.forEach {
        if (it.enabled && (it.key.trim() != "" || it.value.trim() != "")) {
            when (it.type) {
                FormDataItem.File -> {
                    val filePath = it.value.trim()
                    if (filePath.isNotEmpty()) {
                        val absolutePath = getAbsolutePath(path, filePath)
                        val file = File(absolutePath)
                        if (file.exists()) {
                            builder.append("--$boundary\r\n")
                            builder.append("Content-Disposition: form-data; name=\"${it.key}\"; filename=\"${file.name}\"\r\n")
                            builder.append("Content-Type: ${Files.probeContentType(file.toPath()) ?: "application/octet-stream"}\r\n\r\n")
                            builder.append(file.readBytes().toString(Charsets.ISO_8859_1))
                            builder.append("\r\n")
                        }
                    }
                }

                FormDataItem.Xml, FormDataItem.Json, FormDataItem.Number, FormDataItem.Boolean, FormDataItem.Text -> {
                    builder.append("--$boundary\r\n")
                    builder.append("Content-Disposition: form-data; name=\"${it.key}\"\r\n\r\n")
                    builder.append(it.value)
                    builder.append("\r\n")
                }
            }
        }
    }

    builder.append("--$boundary--\r\n")
    return BodyPublishers.ofString(builder.toString())
}

private fun handleUrlEncoded(api: ApiData): BodyPublisher {
    val encodedParams = StringBuilder()

    api.formData.forEachIndexed { index, item ->
        if (item.enabled && (item.key.trim().isNotEmpty() || item.value.trim().isNotEmpty())) {
            if (encodedParams.isNotEmpty()) {
                encodedParams.append("&")
            }
            val encodedKey = URLEncoder.encode(item.key, StandardCharsets.UTF_8.toString())
            val encodedValue = URLEncoder.encode(item.value, StandardCharsets.UTF_8.toString())
            encodedParams.append("$encodedKey=$encodedValue")
        }
    }

    return BodyPublishers.ofString(encodedParams.toString())
}

fun handleOkBodyType(api: ApiData, path: String): RequestBody? {
    if (api.requestDataType == RequestDataType.RawJs) {
        return api.js?.toRequestBody()
    }
    if (api.requestDataType == RequestDataType.RawXml) {
        return api.xml?.toRequestBody()
    }
    if (api.requestDataType == RequestDataType.RawHtml) {
        return api.html?.toRequestBody()
    }
    if (api.requestDataType == RequestDataType.RawText) {
        return api.text?.toRequestBody()
    }
    if (api.requestDataType == RequestDataType.RawJson) {
        return api.json?.toRequestBody()
    }
    if (api.requestDataType == RequestDataType.FormData) {
        return handleOkFormData(api, path)
    }
    if (api.requestDataType == RequestDataType.UrlEncoded) {
        return handleOkUrlEncoded(api)
    }
    if(api.requestDataType == RequestDataType.Binary){
        if(api.binary != null) {
            val absolutePath = getAbsolutePath(path, api.binary)
            val file = File(absolutePath)
            if(file.exists()){
                val mediaType = "application/octet-stream".toMediaTypeOrNull() // Adjust as necessary
                return file.asRequestBody(mediaType)
            }
        }
    }
    return null
}

fun handleOkFormData(api: ApiData, path: String): RequestBody {
    val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
    api.formData.forEach {
        if (it.enabled && it.key.isNotBlank()) {
            when (it.type) {
                FormDataItem.File -> {
                    val filePath = it.value.trim()
                    if (filePath.isNotEmpty()) {
                        val absolutePath = getAbsolutePath(path, filePath)
                        val file = File(absolutePath)
                        if (file.exists()) {
                            multipartBuilder.addFormDataPart(
                                it.key,
                                file.name,
                                file.asRequestBody()
                            )
                        }
                    }
                }

                FormDataItem.Xml, FormDataItem.Json, FormDataItem.Number, FormDataItem.Boolean, FormDataItem.Text -> {
                    multipartBuilder.addFormDataPart(it.key, it.value)
                }
            }
        }
    }
    return multipartBuilder.build()
}

fun handleOkUrlEncoded(api: ApiData): RequestBody {
    val formBuilder = FormBody.Builder()
    api.formData.forEach {
        if (it.enabled && it.key.isNotBlank() && it.value.isNotBlank()) {
            formBuilder.add(it.key, it.value)
        }
    }
    return formBuilder.build()
}
