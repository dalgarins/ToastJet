package com.aayam.toasteditor.browserHandler

import org.cef.callback.CefCallback
import org.cef.handler.CefResourceHandler
import org.cef.misc.IntRef
import org.cef.misc.StringRef
import org.cef.network.CefRequest
import org.cef.network.CefResponse
import java.io.InputStream

class CustomResourceHandler : CefResourceHandler {
    private var inputStream: InputStream? = null

    override fun processRequest(
        request: CefRequest, callback: CefCallback
    ): Boolean {
        val resourcePath = request.url.replace("local://myapp", "html/")
        val resource = javaClass.classLoader.getResource(resourcePath)

        if (resource != null) {
            val connection = resource.openConnection()
            inputStream = connection.getInputStream()
            callback.Continue()
            return true
        } else {
            println("Resource not found: $resourcePath")
            return false
        }
    }

    override fun getResponseHeaders(
        response: CefResponse, responseLength: IntRef, redirectUrl: StringRef
    ) {
        if (inputStream != null) {
            response.status = 200
            response.mimeType = "text/html"
            responseLength.set(inputStream!!.available())
        } else {
            response.status = 404
        }
    }

    override fun readResponse(
        dataOut: ByteArray, maxBytesToRead: Int, bytesRead: IntRef, callback: CefCallback
    ): Boolean {
        val available = inputStream?.available() ?: 0
        if (available > 0) {
            val read = inputStream!!.read(dataOut, 0, maxBytesToRead.coerceAtMost(available))
            bytesRead.set(read)
            return true
        }
        inputStream?.close()
        return false
    }

    override fun cancel() {
        inputStream?.close()
    }
}
