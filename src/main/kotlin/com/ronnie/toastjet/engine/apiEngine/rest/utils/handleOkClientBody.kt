package com.ronnie.toastjet.engine.apiEngine.rest.utils

import com.ronnie.toastjet.model.data.GraphQLData
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.FormType
import com.ronnie.toastjet.model.enums.RawType
import com.ronnie.toastjet.swing.store.configStore
import com.ronnie.toastjet.utils.fileUtils.getAbsolutePath
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

fun handleOkClientBody(apiRequest: RequestData): RequestBody? {
    return when (apiRequest.bodyTypeState) {
        BodyType.None -> null
        BodyType.FormData -> handleOkFormData(apiRequest)
        BodyType.URLEncoded -> handleOkUrlEncoded(apiRequest)
        BodyType.Binary -> handleBinaryBody(apiRequest)
        BodyType.RAW -> {
            when (apiRequest.rawTypeState) {
                RawType.JSON -> apiRequest.json.toRequestBody()
                RawType.XML -> apiRequest.xml.toRequestBody()
                RawType.TEXT -> apiRequest.text.toRequestBody()
                RawType.HTML -> apiRequest.html.toRequestBody()
                RawType.JS -> apiRequest.js.toRequestBody()
                RawType.GraphQL -> handleGraphQLData(apiRequest.graphQl)
            }
        }
    }
}

fun handleGraphQLData(data: GraphQLData): RequestBody? {
    val graphQLPayload = """
                        {
                            "query": "${data.query.replace("\"", "\\\"")}",
                            "variables": ${data.variable.ifBlank { "{}" }}
                        }
                    """.trimIndent()

    return graphQLPayload.toRequestBody("application/json".toMediaTypeOrNull())
}

fun handleOkFormData(api: RequestData): RequestBody? {
    configStore?.let { configStore ->
        val multipartBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        api.formData.forEach {
            if (it.enabled && it.key.isNotBlank()) {
                when (it.type) {
                    FormType.File -> {
                        val filePath = it.value.trim()
                        if (filePath.isNotEmpty()) {
                            val absolutePath = getAbsolutePath(configStore.appState.file.path, filePath)
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

                    FormType.Text -> {
                        multipartBuilder.addFormDataPart(it.key, it.value)
                    }
                }
            }
        }
        return multipartBuilder.build()
    }
    return null
}


fun handleBinaryBody(apiRequest: RequestData): RequestBody? {
    val appState = configStore?.appState
    appState?.let { appState ->
        val absolutePath = getAbsolutePath(appState.file.path, apiRequest.binary)
        val file = File(absolutePath)
        if (file.exists()) {
            val mediaType = "application/octet-stream".toMediaTypeOrNull()
            return file.asRequestBody(mediaType)
        }
    }
    return null
}

fun handleOkUrlEncoded(apiRequest: RequestData): RequestBody {
    val formBuilder = FormBody.Builder()
    apiRequest.urlEncoded.forEach {
        if (it.isChecked && it.key.isNotBlank() && it.value.isNotBlank()) {
            formBuilder.add(it.key, it.value)
        }
    }
    return formBuilder.build()
}
