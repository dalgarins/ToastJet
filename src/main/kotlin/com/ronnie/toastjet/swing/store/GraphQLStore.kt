package com.ronnie.toastjet.swing.store

import com.google.gson.Gson
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.vfs.writeText
import com.ronnie.toastjet.model.data.AllGraphQLSchemaInfo
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.GraphQLData
import com.ronnie.toastjet.model.data.GraphQLRequestData
import com.ronnie.toastjet.model.data.GraphQLResponseData
import com.ronnie.toastjet.model.data.KeyValue
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.utils.generateRandomUuid
import java.io.File
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class GraphQLStore(
    val appStore: AppStore
) {

    private val gson = Gson()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var saveTask: Runnable? = null

    val theme = StateHolder(EditorColorsManager.getInstance())
    val methodState = StateHolder(HttpMethod.POST)
    val nameState = StateHolder("")
    val urlState = StateHolder("")
    val invokedAtState = StateHolder<Date?>(null)
    val paramsState = StateHolder<MutableList<KeyValueChecked>>(mutableListOf())
    val headersState = StateHolder<MutableList<KeyValueChecked>>(mutableListOf())
    val pathState = StateHolder<MutableList<KeyValue>>(mutableListOf())
    val cookieState = StateHolder<MutableList<CookieData>>(mutableListOf())
    val testState = StateHolder("")
    val graphQLSchema = StateHolder(AllGraphQLSchemaInfo())
    val graphQLState = StateHolder(GraphQLData())

    var id: String = ""

    val response = StateHolder(GraphQLResponseData())

    fun loadStatesFromRequestData(requestData: GraphQLRequestData) {
        urlState.setState(requestData.url)
        nameState.setState(requestData.name)
        methodState.setState(requestData.method)
        headersState.setState(requestData.headers.toMutableList())
        paramsState.setState(requestData.params.toMutableList())
        pathState.setState(requestData.path.toMutableList())
        invokedAtState.setState(requestData.invokedAt)
        graphQLState.setState(requestData.graphQL)
        cookieState.setState(requestData.cookie.toMutableList())
        testState.setState(requestData.test)
        graphQLSchema.setState(requestData.graphQLSchema)
        id = if (requestData.id.trim().isEmpty()) generateRandomUuid() else requestData.id
    }

    fun getCurrentGraphQLFromStates(): GraphQLRequestData {
        return GraphQLRequestData(
            url = urlState.getState(),
            name = nameState.getState(),
            method = methodState.getState(),
            headers = headersState.getState(),
            params = paramsState.getState(),
            path = pathState.getState(),
            invokedAt = invokedAtState.getState(),
            graphQL = graphQLState.getState(),
            cookie = cookieState.getState(),
            id = id,
            test = testState.getState(),
            graphQLSchema = graphQLSchema.getState()
        )
    }

    private fun scheduleSave() {
        saveTask?.let { executor.schedule({}, 0, TimeUnit.MILLISECONDS) }
        saveTask = Runnable {
            saveRequest()
        }
        saveTask?.let { executor.schedule(it, 500, TimeUnit.MILLISECONDS) }
    }

    private fun saveResponse(responseData: GraphQLResponseData) {
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

    fun loadResponse(id: String): GraphQLResponseData {
        return try {
            val responseFile = File(System.getProperty("user.home"), ".toastApi/response/$id.json")
            if (!responseFile.exists()) {
                println("Response file does not exist.")
                return GraphQLResponseData()
            }

            val json = responseFile.readText()
            val res = gson.fromJson(json, GraphQLResponseData::class.java)
            res
        } catch (e: Exception) {
            println("Failed to load response from file: ${e.message}")
            e.printStackTrace()
            GraphQLResponseData()
        }
    }

    private fun saveRequest() {
        val json = gson.toJson(getCurrentGraphQLFromStates())

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
                    val req = gson.fromJson(requestText, GraphQLRequestData::class.java)
                    loadStatesFromRequestData(req)
                    val res = loadResponse(req.id)
                    response.setState(res)
                }
            } catch (_: Exception) {
                loadStatesFromRequestData(GraphQLRequestData())
            }
        }

        response.addListener(this::saveResponse)
        urlState.addListener { scheduleSave() }
        nameState.addListener { scheduleSave() }
        methodState.addListener { scheduleSave() }
        headersState.addListener { scheduleSave() }
        paramsState.addListener { scheduleSave() }
        pathState.addListener { scheduleSave() }
        invokedAtState.addListener { scheduleSave() }
        graphQLState.addListener { scheduleSave() }
        cookieState.addListener { scheduleSave() }
        testState.addListener { scheduleSave() }
        graphQLSchema.addListener { scheduleSave() }
    }
}