package com.ronnie.toastjet.engine.apiEngine.socket

import com.ronnie.toastjet.engine.apiEngine.getOkClient
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handlePath
import com.ronnie.toastjet.model.data.SocketMessageData
import com.ronnie.toastjet.model.data.SocketResponseData
import com.ronnie.toastjet.swing.store.SocketStore
import com.ronnie.toastjet.swing.store.configStore
import com.ronnie.toastjet.utils.apiUtils.extractCookies
import io.ktor.http.HttpStatusCode
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.time.Duration
import java.time.LocalDateTime
import javax.swing.SwingUtilities

object SocketClient {

    var okClient: OkHttpClient? = null
    var webSocket: WebSocket? = null


    fun connect(store: SocketStore) {

        val requestUrl = handlePath(
            store.urlState.getState(),
            configStore?.state?.getState()?.baseDomain,
            store.pathState.getState()
        )
        val requestBuilder = Request.Builder()
            .url(requestUrl)

        okClient = getOkClient(configStore!!, store.cookieState.getState())
        val requestHeaders = mutableMapOf<String, String>()
        store.headersState.getState().forEach {
            if (it.isChecked) {
                requestHeaders[it.key] = it.value
                requestBuilder.addHeader(it.key, it.value)
            }
        }

        val request = requestBuilder.build()

        val startTime = LocalDateTime.now()
        webSocket = okClient!!.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                val endTime = LocalDateTime.now()
                val body = response.body?.string()
                val respHeaders = response.headers.toMultimap().mapValues { it.value.joinToString(", ") }
                val res = SocketResponseData(
                    invoked = true,
                    isBeingInvoked = false,
                    url = requestUrl,
                    name = "",
                    description = "",
                    requestHeaders = requestHeaders,
                    responseHeaders = respHeaders,
                    errorMessage = ArrayList(),
                    size = body?.length ?: 0,
                    status = response.code,
                    statusText = HttpStatusCode.fromValue(response.code).description,
                    data = body,
                    error = false,
                    setCookie = extractCookies(respHeaders),
                    timeTaken = Duration.between(startTime, endTime).toMillis()
                )
                store.socketConnected.setState(true)
                println("We are connected")
                store.connectResponse.setState(res)
            }

            override fun onMessage(ws: WebSocket, text: String) {
                println("📩 Received text: $text")
                SwingUtilities.invokeLater {
                    store.messagesState.setState { currentList ->
                        currentList.add(
                            SocketMessageData(
                                text,
                                java.util.Date(),
                                false
                            )
                        )
                        currentList
                    }
                }
            }

            override fun onMessage(ws: WebSocket, bytes: ByteString) {
                val hexString = bytes.hex()
                println("📩 Received bytes: $hexString")
                SwingUtilities.invokeLater {
                    store.messagesState.setState { currentList ->
                        currentList.add(
                            SocketMessageData(
                                hexString,
                                java.util.Date(),
                                false
                            )
                        )
                        currentList
                    }
                }
            }

            override fun onClosing(ws: WebSocket, code: Int, reason: String) {
                println("❗ Closing: $code / $reason")
                ws.close(1000, null)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                val endTime = LocalDateTime.now()
                val body = response?.body?.string()
                val respHeaders = response?.headers?.toMultimap()?.mapValues { it.value.joinToString(", ") }
                val res = SocketResponseData(
                    invoked = true,
                    isBeingInvoked = false,
                    url = requestUrl,
                    name = "",
                    description = "",
                    requestHeaders = requestHeaders,
                    responseHeaders = respHeaders ?: emptyMap(),
                    errorMessage = ArrayList(),
                    size = body?.length ?: 0,
                    status = response?.code ?: 400,
                    statusText = HttpStatusCode.fromValue(response?.code ?: 400).description,
                    data = body,
                    error = false,
                    setCookie = if (respHeaders == null) emptyList() else extractCookies(respHeaders),
                    timeTaken = Duration.between(startTime, endTime).toMillis()
                )
                store.socketConnected.setState(false)
                store.connectResponse.setState(res)
            }
        })
    }


    fun sendMessage(store: SocketStore) {
        if (webSocket != null) {
            store.messagesState.setState { currentList ->
                currentList.add(
                    SocketMessageData(
                        store.messageList.getState()[store.selectedMessage.getState()].message,
                        java.util.Date(),
                        true
                    )
                )
                currentList
            }
            if (store.selectedResMessage.getState() == -1 && store.messagesState.getState().isNotEmpty()) {
                store.selectedResMessage.setState(store.messagesState.getState().size - 1)
            }
            webSocket!!.send(store.messageList.getState()[store.selectedMessage.getState()].message)
        }
    }

    fun disconnect(store: SocketStore) {
        if (webSocket != null) {
            webSocket!!.close(1000, "Goodbye!")
            store.socketConnected.setState(false)
        }
    }

}