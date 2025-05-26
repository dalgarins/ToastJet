package com.ronnie.toastjet.swing.components.apiPanels

import com.google.gson.Gson
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.engine.apiEngine.rest.invokeRestApi
import com.ronnie.toastjet.engine.scriptExecutor.ScriptExecutor
import javax.swing.JPanel
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.store.configStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.awt.BorderLayout
import java.awt.Dimension
import java.util.Date
import javax.swing.*

class ResponseComponent(private val store: RequestStore) : JPanel() {

    val gson = Gson()

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        preferredSize = Dimension(300, 500)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        isOpaque = true
        generatePanel()
        add(JLabel("Not invoked"))
        store.response.addListener {
            generatePanel()
        }
    }

    fun generatePanel() {
        this.removeAll()
        println("Are we called ${store.response.getState().isBeingInvoked} ${store.response.getState().invoked}")

        if (store.response.getState().isBeingInvoked) {
            val loading = JPanel(BorderLayout()).apply {
                add(JLabel("Request is being invoked..."), BorderLayout.NORTH)
                add(JProgressBar().apply {
                    isIndeterminate = true
                    isBorderPainted = false
                }, BorderLayout.CENTER)
            }
            add(loading)
            this.repaint()
            this.revalidate()
            configStore?.let { configStore ->
                println("Do we reach here ")
                coroutineScope.launch {
                    println("Do we reach here part 2")
                    val timeNow = Date()
                    val request = ScriptExecutor.executeJsCode(
                        gson.toJson(
                            mapOf(
                                Pair("config", configStore.state.getState()),
                                Pair("api", store.state.getState())
                            )
                        )
                    )
                    val timeLater = Date()
                    println("Do we reach here part 2")

                    val response = invokeRestApi(request)
                    println("Do we reach here part 2")

                    val timeAfter = Date()
                    println("Script Execution = ${timeLater.time - timeNow.time} , api invocation = ${timeAfter.time - timeLater.time}")

                    println("Do we reach here part 3")
                    SwingUtilities.invokeLater {
                        remove(loading)
                        store.response.setState(response)
                        revalidate()
                        repaint()
                    }
                    println("Do we reach here part 4")

                }
            }
        } else {
            if (store.response.getState().invoked) {
                add(JLabel("Status : ${store.response.getState().status}"))
            } else {
                add(JPanel(BorderLayout()).apply {
                    add(JLabel("Request not invoked"))
                })
            }
            this.repaint()
            this.revalidate()
        }

    }

}

