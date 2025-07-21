package com.ronnie.toastjet.engine.apiEngine.socket

import com.ronnie.toastjet.engine.apiEngine.rest.utils.handlePath
import com.ronnie.toastjet.model.data.SocketMessageData
import com.ronnie.toastjet.model.data.SocketResponseData
import com.ronnie.toastjet.swing.store.SocketStore
import com.ronnie.toastjet.swing.store.configStore
import io.socket.client.IO
import io.socket.client.Socket
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date
import javax.swing.SwingUtilities

object SocketIoClient {

    var socket: Socket? = null

    fun connect(store: SocketStore) {
        val requestUrl = handlePath(
            store.urlState.getState(),
            configStore?.state?.getState()?.baseDomain,
            store.pathState.getState()
        )

        val startTime = LocalDateTime.now()

        try {
            val headers = mutableMapOf<String, String>()
            store.headersState.getState().forEach {
                if (it.isChecked) {
                    headers[it.key] = it.value
                }
            }

            val opts = IO.Options()
            opts.transports = arrayOf("websocket")
            opts.extraHeaders = headers.mapValues {
                listOf(it.value)
            }

            socket = IO.socket(URI.create(requestUrl), opts)

            // EVENT: Connect
            socket!!.on(Socket.EVENT_CONNECT) {
                val endTime = LocalDateTime.now()
                val res = SocketResponseData(
                    invoked = true,
                    isBeingInvoked = false,
                    url = requestUrl,
                    name = "",
                    description = "",
                    requestHeaders = headers,
                    responseHeaders = emptyMap(),
                    errorMessage = arrayListOf(),
                    size = 0,
                    status = 200,
                    statusText = "Connected",
                    data = "",
                    error = false,
                    setCookie = emptyList(),
                    timeTaken = Duration.between(startTime, endTime).toMillis()
                )
                store.socketConnected.setState(true)
                store.connectResponse.setState(res)
                println("Socket.IO connected")
            }

            store.eventList.getState().forEach { eventEntry ->
                socket!!.on(eventEntry.key) { args ->
                    val messageContent: Any? = args.firstOrNull()
                    store.messagesState.setState { currentMessages ->
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

            // EVENT: Disconnect
            socket!!.on(Socket.EVENT_DISCONNECT) { args ->
                val reason = args.getOrNull(0)?.toString() ?: "Unknown"
                val endTime = LocalDateTime.now()
                val res = SocketResponseData(
                    invoked = true,
                    isBeingInvoked = false,
                    url = requestUrl,
                    name = "",
                    description = "",
                    requestHeaders = headers,
                    responseHeaders = emptyMap(),
                    errorMessage = arrayListOf(),
                    size = 0,
                    status = 200,
                    statusText = "Disconnected",
                    data = "",
                    error = false,
                    setCookie = emptyList(),
                    timeTaken = Duration.between(startTime, endTime).toMillis()
                )
                store.socketConnected.setState(false)
                store.connectResponse.setState(res)
                println("Socket.IO disconnected: $reason")
            }

            socket!!.on(Socket.EVENT_CONNECT_ERROR) { args ->
                val error = args.getOrNull(0)?.toString() ?: "Unknown error"
                println("❗ Socket.IO Error: $error")
                SwingUtilities.invokeLater {
                    store.messagesState.setState { currentList ->
                        currentList.add(
                            SocketMessageData(
                                "Error: $error",
                                Date(),
                                false
                            )
                        )
                        currentList
                    }
                }
            }

            socket!!.connect()

        } catch (e: Exception) {
            e.printStackTrace()
            val endTime = LocalDateTime.now()
            val res = SocketResponseData(
                invoked = true,
                isBeingInvoked = false,
                url = requestUrl,
                name = "",
                description = "",
                requestHeaders = mapOf(),
                responseHeaders = emptyMap(),
                errorMessage = arrayListOf(e.message ?: "Connection failed"),
                size = 0,
                status = 400,
                statusText = "Error",
                data = "",
                error = true,
                setCookie = emptyList(),
                timeTaken = Duration.between(startTime, endTime).toMillis()
            )
            store.socketConnected.setState(false)
            store.connectResponse.setState(res)
        }
    }

    fun sendMessage(store: SocketStore) {
        socket?.let { s ->
            val msg = store.messageList.getState()[store.selectedMessage.getState()].message
            s.emit("", msg)

            SwingUtilities.invokeLater {
                store.messagesState.setState { currentList ->
                    currentList.add(
                        SocketMessageData(
                            msg,
                            Date(),
                            true
                        )
                    )
                    currentList
                }
            }

            if (store.selectedResMessage.getState() == -1 && store.messagesState.getState().isNotEmpty()) {
                store.selectedResMessage.setState(store.messagesState.getState().size - 1)
            }
        }
    }

    fun disconnect(store: SocketStore) {
        socket?.disconnect()
        socket = null
        store.socketConnected.setState(false)
    }
}