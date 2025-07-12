package com.ronnie.toastjet.engine.apiEngine.socket

import com.ronnie.toastjet.engine.apiEngine.rest.utils.handlePath
import com.ronnie.toastjet.model.data.SocketMessageData
import com.ronnie.toastjet.model.data.SocketResponseData
import com.ronnie.toastjet.swing.store.SocketStore
import com.ronnie.toastjet.swing.store.configStore
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import java.net.URI
import java.time.Duration
import java.time.LocalDateTime
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
            // Prepare headers
            val headers = mutableMapOf<String, String>()
            store.headersState.getState().forEach {
                if (it.isChecked) {
                    headers[it.key] = it.value
                }
            }

            // Setup Socket.IO options
            val opts = IO.Options()
            opts.transports = arrayOf("websocket") // Use only WebSocket
            opts.extraHeaders = headers.mapValues {
                listOf(it.value)
            }

            socket = IO.socket(URI.create(requestUrl), opts)

            // EVENT: Connect
            socket!!.on(Socket.EVENT_CONNECT, Emitter.Listener {
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
                    setCookie = emptyList(), // TODO: Extract cookies if needed
                    timeTaken = Duration.between(startTime, endTime).toMillis()
                )
                store.socketConnected.setState(true)
                store.connectResponse.setState(res)
                println("Socket.IO connected")
            })

            // EVENT: Disconnect
            socket!!.on(Socket.EVENT_DISCONNECT, Emitter.Listener { args ->
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
            })

            // EVENT: Message
            socket!!.on("", Emitter.Listener { args ->
                val message = args[0].toString()
                println("📩 Received message: $message")

                SwingUtilities.invokeLater {
                    store.messagesState.setState { currentList ->
                        currentList.add(
                            SocketMessageData(
                                message,
                                java.util.Date(),
                                false
                            )
                        )
                        currentList
                    }
                }
            })

            // EVENT: Error
            socket!!.on(Socket.EVENT_CONNECT_ERROR, Emitter.Listener { args ->
                val error = args.getOrNull(0)?.toString() ?: "Unknown error"
                println("❗ Socket.IO Error: $error")
                SwingUtilities.invokeLater {
                    store.messagesState.setState { currentList ->
                        currentList.add(
                            SocketMessageData(
                                "Error: $error",
                                java.util.Date(),
                                false
                            )
                        )
                        currentList
                    }
                }
            })

            // Start connection
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
                            java.util.Date(),
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
        store.socketConnected.setState(false)
        println("Socket.IO disconnected manually")
    }
}