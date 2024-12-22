package com.aayam.toasteditor.browserHandler

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLoadHandler
import org.cef.network.CefRequest

class BrowserLoadHandler(private val browser: JBCefBrowser) : CefLoadHandler {

    override fun onLoadingStateChange(
        browser: CefBrowser?, isLoading: Boolean, canGoBack: Boolean, canGoForward: Boolean
    ) {
    }

    override fun onLoadStart(
        browser: CefBrowser?, frame: CefFrame?, transitionType: CefRequest.TransitionType?
    ) {
    }

    override fun onLoadEnd(browser: CefBrowser?, frame: CefFrame?, httpStatusCode: Int) {
        val connection = ApplicationManager.getApplication().messageBus.connect()
        val theme = EditorColorsManager.getInstance().isDarkEditor

        connection.subscribe(
            EditorColorsManager.TOPIC,
            EditorColorsListener {
                frame?.executeJavaScript(
                    """window.onInitialData({theme:${if(theme) 1 else 0}})""",
                    frame.url, 0
                )
            }
        )

//        frame?.executeJavaScript(
//            """window.onInitialData({theme:${if(theme) 1 else 0}})""",
//            frame.url, 0
//        )
        this.browser.openDevtools()
    }

    override fun onLoadError(
        browser: CefBrowser?, frame: CefFrame?, errorCode: CefLoadHandler.ErrorCode?,
        errorText: String?, failedUrl: String?
    ) {
        println("Error loading $failedUrl: $errorText")
    }
}
