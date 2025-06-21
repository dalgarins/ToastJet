package com.ronnie.toastjet.swing.rest.components.apiPanels

import com.google.gson.Gson
import com.ronnie.toastjet.engine.apiEngine.rest.invokeRestApi
import com.ronnie.toastjet.engine.scriptExecutor.ScriptExecutor
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.ResponseInvoked
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.ResponseLoading
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.ResponseNotInvoked
import javax.swing.JPanel
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.store.configStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.swing.Swing
import kotlinx.coroutines.withContext
import java.awt.Dimension
import javax.swing.*

class ResponseComponent(private val store: RequestStore) : JPanel() {

    val gson = Gson()

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun setTheme() {
        val theme = store.theme.getState()
        background = theme.globalScheme.defaultBackground
    }

    fun invoke() {
        configStore?.let { configStore ->
            coroutineScope.launch {
                val request = ScriptExecutor.executePrescriptCode(
                    gson.toJson(
                        mapOf(
                            Pair("config", configStore.state.getState()),
                            Pair("api", store.getCurrentRequestDataFromStates())
                        )
                    )
                )
                println("Do we reach here")
                withContext(Dispatchers.Swing) {
                    val response = invokeRestApi(request)
                    SwingUtilities.invokeLater {
                        removeAll()
                        store.response.setState(response)
                        revalidate()
                        repaint()
                        val reqRes = ScriptExecutor.executePostscriptCode(
                            gson.toJson(
                                mapOf(
                                    Pair("req", store.getCurrentRequestDataFromStates()),
                                    Pair("res", store.response.getState()),
                                    Pair("config", configStore.state.getState())
                                )
                            )
                        )
                        println("The response after post script is ${reqRes.res.tests}")
                        removeAll()
                        store.response.setState(reqRes.res)
                        revalidate()
                        repaint()
                    }
                }

            }
        }
    }

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        preferredSize = Dimension(500, preferredSize.height)
        setTheme()
        isOpaque = true
        generatePanel()
        store.response.addListener { generatePanel() }
        store.theme.addListener { setTheme() }
    }

    fun generatePanel() {
        this.removeAll()
        if (store.response.getState().isBeingInvoked) {
            add(ResponseLoading(store))
            invoke()
        } else {
            if (store.response.getState().invoked) {
                add(ResponseInvoked(store))
            } else {
                add(ResponseNotInvoked(store.theme))
            }
        }
        this.repaint()
        this.revalidate()
    }
}

