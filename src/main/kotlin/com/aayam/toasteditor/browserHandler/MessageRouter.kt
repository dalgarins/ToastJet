package com.aayam.toasteditor.browserHandler

import com.aayam.toasteditor.constants.enums.MessageType
import com.aayam.toasteditor.constants.interfaces.apis.ApiData
import com.aayam.toasteditor.constants.interfaces.message.MessageData
import com.aayam.toasteditor.constants.interfaces.message.SaveRequestApiData
import com.aayam.toasteditor.messageHandler.fileHandler.fileDeleteHandler
import com.aayam.toasteditor.messageHandler.fileHandler.filePickerHandler
import com.aayam.toasteditor.messageHandler.fileHandler.fileSaverHandler
import com.aayam.toasteditor.messageHandler.initializeHandler
import com.aayam.toasteditor.messageHandler.requestHandler.getRawRequestHandler
import com.aayam.toasteditor.messageHandler.requestHandler.getResponseFromNonce
import com.aayam.toasteditor.messageHandler.requestHandler.getResponseHandler
import com.aayam.toasteditor.messageHandler.requestHandler.saveRequestHandler
import com.aayam.toasteditor.messageHandler.variableHandler.getVariableHandler
import com.aayam.toasteditor.messageHandler.variableHandler.loadEnvironmentHandler
import com.aayam.toasteditor.messageHandler.variableHandler.saveVariablesHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.callback.CefQueryCallback
import org.cef.handler.CefMessageRouterHandler

class MessageRouter(val browser: JBCefBrowser, val document: VirtualFile, val project: Project) :
    CefMessageRouterHandler {


    override fun getNativeRef(identifier: String?): Long {
        return 0
    }

    override fun setNativeRef(identifier: String?, nativeRef: Long) {
    }

    override fun onQuery(
        browser: CefBrowser?,
        frame: CefFrame?,
        queryId: Long,
        request: String?,
        persistent: Boolean,
        callback: CefQueryCallback?
    ): Boolean {
        request?.let {
            val message = Json.decodeFromString(MessageData.serializer(String.serializer().nullable), it)

            when (message.type) {
                MessageType.Initialize -> {
                    val res = initializeHandler(this.document)
                    callback?.success(res)
                    return true
                }

                MessageType.FilePicker -> {
                    val res = filePickerHandler(project, document)
                    callback?.success(res)
                    return true
                }

                MessageType.FileSaver -> {
                    message.data?.let {
                        val res = fileSaverHandler(data = it, project = this.project)
                        callback?.success(res)
                    }
                    callback?.failure(404, "Data not found")
                    return true
                }

                MessageType.FileDelete -> {
                    message.data?.let {
                        val res = fileDeleteHandler(
                            currentDir = this.document.path,
                            relativeDir = it
                        )
                        callback?.success(res)
                    }
                }

                MessageType.GetVariables -> {
                    println("Get Variables has been triggered")
                    val resp = getVariableHandler()
                    callback?.success(resp)
                }

                MessageType.SaveVariables -> {
                    message.data?.let {
                        saveVariablesHandler(
                            file = this.document,
                            data = it
                        )
                    }
                }

                MessageType.LoadEnvironment -> {
                    message.data?.let {
                        val response = loadEnvironmentHandler(this.document.path, it)
                        callback?.success(response)
                    }
                }

                MessageType.GetRawRequest -> {
                    val data = getRawRequestHandler()
                    callback?.success(data)
                }

                MessageType.GetResponse -> {
                    message.data?.let{ jsonData ->
                        try{
                            val apiData = Json.decodeFromString(ApiData.serializer(),jsonData)
                            val response = getResponseHandler(apiData)
                            callback?.success(response)
                        }catch(err:Error){
                            println("There is error parsing the getResponse JSON ${err.message} $jsonData",)
                            callback?.failure(400,err.message)
                        }
                    }
                }

                MessageType.GetResponseFromNonce -> {
                    getResponseFromNonce()
                }

                MessageType.SaveRequest -> {
                    val jsonData = message.data
                    if (jsonData != null) {
                        try {
                            val apiInfo = Json.decodeFromString(SaveRequestApiData.serializer(), jsonData)
                            saveRequestHandler(
                                apiInfo = apiInfo,
                                file = document
                            )
                        } catch (err: Exception) {
                            println("Error parsing save request API data: ${err.message} $jsonData")
                        }
                    } else {
                        println("Message data is null for SaveRequest")
                    }
                }

            }
        }
        return true
    }

    override fun onQueryCanceled(browser: CefBrowser?, frame: CefFrame?, queryId: Long) {
        println("Query canceled: $queryId")
    }
}