package com.aayam.toasteditor.browserHandler

import com.aayam.toasteditor.constants.interfaces.MessageData
import com.aayam.toasteditor.constants.enums.MessageType
import com.aayam.toasteditor.messageHandler.fileHandler.fileDeleteHandler
import com.aayam.toasteditor.messageHandler.requestHandler.getRawRequestHandler
import com.aayam.toasteditor.messageHandler.requestHandler.getResponseFromNonce
import com.aayam.toasteditor.messageHandler.requestHandler.getResponseHandler
import com.aayam.toasteditor.messageHandler.requestHandler.saveRequestHandler
import com.aayam.toasteditor.messageHandler.variableHandler.getVariableHandler
import com.aayam.toasteditor.messageHandler.variableHandler.saveVariablesHandler
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import filePickerHandler
import fileSaverHandler
import initializeHandler
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
            val message = Json.decodeFromString(MessageData.serializer(String.serializer().nullable), it);

            when (message.type) {
                MessageType.Initialize -> {
                    val res = initializeHandler(this.document)
                    callback?.success(res)
                    return true
                }

                MessageType.FilePicker -> {
                    val res = filePickerHandler(project, document)
                    callback?.success(res);
                    return true
                }

                MessageType.FileSaver -> {
                    message.data?.let {
                        val res = fileSaverHandler(
                            data = it, project = this.project
                        );
                        callback?.success(res)
                    }
                    callback?.failure(404,"Data not found")
                    return true
                }
                MessageType.FileDelete -> {
                    message.data?.let{
                        val res = fileDeleteHandler(
                            currentDir = this.document.path,
                            relativeDir = it
                        );
                        callback?.success(res)
                    }
                }
                MessageType.GetVariables ->{
                    val resp = getVariableHandler()
                    println("The data we got is $resp")
                    callback?.success(resp)
                }
                MessageType.SaveVariables ->{
                    message.data?.let {
                        saveVariablesHandler(
                            file = this.document,
                            data = it
                        )    
                    }
                }
                MessageType.GetRawRequest->{
                    getRawRequestHandler()
                }
                MessageType.GetResponse->{
                    getResponseHandler()
                }
                MessageType.GetResponseFromNonce->{
                    getResponseFromNonce()
                }
                MessageType.SaveRequest ->{
                    saveRequestHandler()
                }
            }
        }
        return true
    }

    override fun onQueryCanceled(browser: CefBrowser?, frame: CefFrame?, queryId: Long) {
        println("Query canceled: $queryId")
    }
}