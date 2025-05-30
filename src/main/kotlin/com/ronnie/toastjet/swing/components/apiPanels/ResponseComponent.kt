package com.ronnie.toastjet.swing.components.apiPanels

import com.google.gson.Gson
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.engine.apiEngine.rest.invokeRestApi
import com.ronnie.toastjet.engine.scriptExecutor.ScriptExecutor
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.ResponseInvoked
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.ResponseLoading
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.ResponseNotInvoked
import javax.swing.JPanel
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.store.configStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import javax.swing.*

class ResponseComponent(private val store: RequestStore) : JPanel() {

    val gson = Gson()

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        isOpaque = true
        generatePanel()
        store.response.addListener {
            generatePanel()
        }
    }

    fun generatePanel() {
        this.removeAll()
        if (store.response.getState().isBeingInvoked) {
            add(ResponseLoading())
            this.repaint()
            this.revalidate()
            configStore?.let { configStore ->
                coroutineScope.launch {
                    val request = ScriptExecutor.executeJsCode(
                        gson.toJson(
                            mapOf(
                                Pair("config", configStore.state.getState()),
                                Pair("api", store.getCurrentRequestDataFromStates())
                            )
                        )
                    )
                    withContext(Dispatchers.Swing) {
                        val response = invokeRestApi(request)
                        SwingUtilities.invokeLater {
                            removeAll()
                            store.response.setState(response)
                            revalidate()
                            repaint()
                        }
                    }
                }
            }
        } else {
            if (store.response.getState().invoked) {
                add(ResponseInvoked(store))
            } else {
                add(ResponseNotInvoked())
            }
            this.repaint()
            this.revalidate()
        }

    }

}

