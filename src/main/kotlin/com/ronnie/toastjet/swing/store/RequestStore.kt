package com.ronnie.toastjet.swing.store

import com.google.gson.Gson
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.vfs.writeText
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.FormData
import com.ronnie.toastjet.model.data.GraphQLData
import com.ronnie.toastjet.model.data.KeyValue
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.data.ResponseData
import com.ronnie.toastjet.model.data.RestResponseData
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.model.enums.RawType
import com.ronnie.toastjet.utils.generateRandomUuid
import java.io.File
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class RequestStore(
    val appStore: AppStore,
) {

    val theme = StateHolder(EditorColorsManager.getInstance())

    private val gson = Gson()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var saveTask: Runnable? = null

    var id = ""
    val urlState = StateHolder("")
    val nameState = StateHolder("")
    val methodState = StateHolder(HttpMethod.GET)
    val bodyTypeState = StateHolder(BodyType.None)
    val rawTypeState = StateHolder(RawType.JSON)
    val expandState = StateHolder(false)
    val headersState = StateHolder(mutableListOf<KeyValueChecked>())
    val paramsState = StateHolder(mutableListOf<KeyValueChecked>())
    val pathState = StateHolder(mutableListOf<KeyValue>())
    val formDataState = StateHolder(mutableListOf<FormData>())
    val urlEncodedState = StateHolder(mutableListOf<KeyValueChecked>())
    val binaryState = StateHolder("")
    val xmlState = StateHolder("")
    val jsonState = StateHolder("")
    val invokedAtState = StateHolder(Date())
    val textState = StateHolder("")
    val graphQlState = StateHolder(GraphQLData())
    val jsState = StateHolder("")
    val htmlState = StateHolder("")
    val cookieState = StateHolder(mutableListOf<CookieData>())
    val testState = StateHolder("")

    val response = StateHolder(RestResponseData())

    fun loadStatesFromRequestData(requestData: RequestData) {
        urlState.setState(requestData.url)
        nameState.setState(requestData.name)
        methodState.setState(requestData.method)
        bodyTypeState.setState(requestData.bodyTypeState)
        rawTypeState.setState(requestData.rawTypeState)
        expandState.setState(requestData.expandState)
        headersState.setState(requestData.headers.toMutableList())
        paramsState.setState(requestData.params.toMutableList())
        pathState.setState(requestData.path.toMutableList())
        formDataState.setState(requestData.formData.toMutableList())
        urlEncodedState.setState(requestData.urlEncoded.toMutableList())
        binaryState.setState(requestData.binary)
        xmlState.setState(requestData.xml)
        jsonState.setState(requestData.json)
        invokedAtState.setState(requestData.invokedAt)
        textState.setState(requestData.text)
        graphQlState.setState(requestData.graphQl)
        jsState.setState(requestData.js)
        htmlState.setState(requestData.html)
        cookieState.setState(requestData.cookie.toMutableList())
        testState.setState(requestData.test)
        id = if (requestData.id.trim().isEmpty()) generateRandomUuid() else requestData.id
    }

    fun getCurrentRequestDataFromStates(): RequestData {
        return RequestData(
            url = urlState.getState(),
            name = nameState.getState(),
            method = methodState.getState(),
            bodyTypeState = bodyTypeState.getState(),
            rawTypeState = rawTypeState.getState(),
            expandState = expandState.getState(),
            headers = headersState.getState(),
            params = paramsState.getState(),
            path = pathState.getState(),
            formData = formDataState.getState(),
            urlEncoded = urlEncodedState.getState(),
            binary = binaryState.getState(),
            xml = xmlState.getState(),
            json = jsonState.getState(),
            invokedAt = invokedAtState.getState(),
            text = textState.getState(),
            graphQl = graphQlState.getState(),
            js = jsState.getState(),
            html = htmlState.getState(),
            cookie = cookieState.getState(),
            id = id,
            test = testState.getState(),
        )
    }

    private fun scheduleSave() {
        saveTask?.let { executor.schedule({}, 0, TimeUnit.MILLISECONDS) }
        saveTask = Runnable {
            saveRequest()
        }
        saveTask?.let { executor.schedule(it, 500, TimeUnit.MILLISECONDS) }
    }

    private fun saveResponse(responseData: ResponseData) {
        try {
            val json = gson.toJson(responseData)
            val responseFile = File(System.getProperty("user.home"), ".toastApi/response/$id.json")
            responseFile.parentFile.mkdirs()
            responseFile.writeText(json)
            println("Saved response to: ${responseFile.absolutePath}")

        } catch (e: Exception) {
            println("Failed to save response to the file")
            e.printStackTrace()
        }
    }

    fun loadResponse(id: String): RestResponseData {
        return try {
            val responseFile = File(System.getProperty("user.home"), ".toastApi/response/$id.json")
            if (!responseFile.exists()) {
                println("Response file does not exist.")
                return RestResponseData()
            }

            val json = responseFile.readText()
            val res = gson.fromJson(json, RestResponseData::class.java)
            res
        } catch (e: Exception) {
            println("Failed to load response from file: ${e.message}")
            e.printStackTrace()
            RestResponseData()
        }
    }

    private fun saveRequest() {
        val json = gson.toJson(getCurrentRequestDataFromStates())

        ApplicationManager.getApplication().invokeLater {
            runWriteAction {
                try {
                    appStore.file.writeText(json)
                } catch (e: Exception) {
                    println("Write failed: ${e.message}")
                }
            }
        }
    }

    init {
        val requestText = appStore.file.readText()
        if (appStore.file.name != "config.toast") {
            try {
                if (requestText.isNotBlank()) {
                    val req = gson.fromJson(requestText, RequestData::class.java)
                    loadStatesFromRequestData(req)
                    val res = loadResponse(req.id)
                    response.setState(res)
                }
            } catch (_: Exception) {
                loadStatesFromRequestData(RequestData())
            }
        }

        response.addListener(this::saveResponse)
        urlState.addListener { scheduleSave() }
        nameState.addListener { scheduleSave() }
        methodState.addListener { scheduleSave() }
        bodyTypeState.addListener { scheduleSave() }
        rawTypeState.addListener { scheduleSave() }
        expandState.addListener { scheduleSave() }
        headersState.addListener { scheduleSave() }
        paramsState.addListener { scheduleSave() }
        pathState.addListener { scheduleSave() }
        formDataState.addListener { scheduleSave() }
        urlEncodedState.addListener { scheduleSave() }
        binaryState.addListener { scheduleSave() }
        xmlState.addListener { scheduleSave() }
        jsonState.addListener { scheduleSave() }
        invokedAtState.addListener { scheduleSave() }
        textState.addListener { scheduleSave() }
        graphQlState.addListener { scheduleSave() }
        jsState.addListener { scheduleSave() }
        htmlState.addListener { scheduleSave() }
        cookieState.addListener { scheduleSave() }
        testState.addListener { scheduleSave() }
    }
}