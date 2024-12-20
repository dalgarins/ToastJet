package com.aayam.toasteditor

import com.aayam.toasteditor.browserHandler.BrowserLoadHandler
import com.aayam.toasteditor.browserHandler.CustomSchemeHandlerFactory
import com.aayam.toasteditor.browserHandler.MessageRouter
import com.aayam.toasteditor.cache.loadDocument
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import org.cef.CefApp
import org.cef.browser.CefMessageRouter
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class TosWebViewEditor(private val project :Project ,private val file: VirtualFile) : FileEditor {
    private val browser = JBCefBrowser( )
    private val router = CefMessageRouter.create()
    private val loader = BrowserLoadHandler(browser)
    private var fileState : FileEditorState? = null;

    init {
        handleMessageRouter()
        browser.jbCefClient.cefClient.addMessageRouter(router)
        browser.jbCefClient.addLoadHandler(loader,browser.cefBrowser)
        registerSchemeHandler()
        loadHtmlContent()
        loadDocument(this.file)
    }

    private fun handleMessageRouter() {
        router.addHandler(MessageRouter(
            browser = this.browser,
            document = this.file,
            project = this.project
        ), true)
    }

    private fun loadHtmlContent() {
        browser.loadURL("local://myapp/index.html")
    }

    private fun registerSchemeHandler() {
        CefApp.getInstance().registerSchemeHandlerFactory(
            "local", "myapp", CustomSchemeHandlerFactory()
        )
    }

    override fun getFile(): VirtualFile = file

    override fun getComponent(): JComponent = browser.component

    override fun getPreferredFocusedComponent(): JComponent = browser.component

    override fun getName(): String = "TOS WebView Editor"

    override fun setState(state: FileEditorState) {
        fileState = state
    }

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun <T : Any?> getUserData(key: Key<T>): T? = null

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {}

    override fun dispose() {
        browser.dispose()
    }
}
