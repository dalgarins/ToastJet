package com.ronnie.toastjet.swing.store

import com.google.gson.Gson
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.vfs.writeText
import com.ronnie.toastjet.engine.apiEngine.socket.SocketIoClient
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.KeyValue
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.model.data.SocketMessage
import com.ronnie.toastjet.model.data.SocketMessageData
import com.ronnie.toastjet.model.data.SocketRequestData
import com.ronnie.toastjet.model.data.SocketResponseData
import com.ronnie.toastjet.model.enums.EditorContentType
import com.ronnie.toastjet.model.enums.SocketType
import com.ronnie.toastjet.utils.generateRandomUuid
import java.util.Date
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class SocketStore(
    val appStore: AppStore
) {
    private val gson = Gson()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var saveTask: Runnable? = null

    val theme = StateHolder(EditorColorsManager.getInstance())
    val nameState = StateHolder("")
    val urlState = StateHolder("")
    val socketState = StateHolder(SocketType.WebSocket)
    val invokedAtState = StateHolder<Date?>(null)
    val paramsState = StateHolder<MutableList<KeyValueChecked>>(mutableListOf())
    val headersState = StateHolder<MutableList<KeyValueChecked>>(mutableListOf())
    val pathState = StateHolder<MutableList<KeyValue>>(mutableListOf())
    val cookieState = StateHolder<MutableList<CookieData>>(mutableListOf())
    val messageList = StateHolder(
        mutableListOf(
            SocketMessage(
                title = "Message 1",
                message = ""
            )
        )
    )
    val selectedMessage = StateHolder(0)
    val content = StateHolder("")
    val contentType = StateHolder(EditorContentType.PlainText)
    val connectResponse = StateHolder(SocketResponseData())
    val messagesState = StateHolder<MutableList<SocketMessageData>>(mutableListOf())
    val socketConnected = StateHolder(false)
    val selectedResMessage = StateHolder(-1)

    val eventList = StateHolder<MutableList<KeyValueChecked>>(mutableListOf())

    var id: String = ""

    fun loadStatesFromRequestData(requestData: SocketRequestData) {
        urlState.setState(requestData.url)
        nameState.setState(requestData.name)
        headersState.setState(requestData.headers.toMutableList())
        paramsState.setState(requestData.params.toMutableList())
        pathState.setState(requestData.path.toMutableList())
        invokedAtState.setState(requestData.invokedAt)
        cookieState.setState(requestData.cookie)
        socketState.setState(requestData.socketType)
        id = if (requestData.id.trim().isEmpty()) generateRandomUuid() else requestData.id
    }

    fun getCurrentGraphQLFromStates(): SocketRequestData {
        return SocketRequestData(
            url = urlState.getState(),
            name = nameState.getState(),
            headers = headersState.getState(),
            params = paramsState.getState(),
            path = pathState.getState(),
            invokedAt = invokedAtState.getState(),
            cookie = cookieState.getState(),
            socketType = socketState.getState(),
            id = id,
            socketMessage = messageList.getState(),
        )
    }

    private fun scheduleSave() {
        saveTask?.let { executor.schedule({}, 0, TimeUnit.MILLISECONDS) }
        saveTask = Runnable {
            saveRequest()
        }
        saveTask?.let { executor.schedule(it, 500, TimeUnit.MILLISECONDS) }
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
                    val req = gson.fromJson(requestText, SocketRequestData::class.java)
                    loadStatesFromRequestData(req)
                }
            } catch (_: Exception) {
                loadStatesFromRequestData(SocketRequestData())
            }
        }

        urlState.addListener { scheduleSave() }
        nameState.addListener { scheduleSave() }
        headersState.addListener { scheduleSave() }
        paramsState.addListener { scheduleSave() }
        pathState.addListener { scheduleSave() }
        invokedAtState.addListener { scheduleSave() }
        cookieState.addListener { scheduleSave() }
        messageList.addListener { scheduleSave() }

        eventList.addListener { events ->
            SocketIoClient.socket?.let { socket ->
                events.forEach { eventEntry ->
                    socket.on(eventEntry.key) { args ->

                        val messageContent: Any? = args.firstOrNull()

                        messagesState.setState { currentMessages ->
                            currentMessages.add(
                                SocketMessageData(
                                    message = messageContent.toString(),
                                    time = Date(),
                                    send = false,
                                    event = eventEntry.key
                                )
                            )
                            currentMessages
                        }
                    }
                }
            }
        }

    }
}