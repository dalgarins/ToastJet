package com.ronnie.toastjet.swing.components.apiPanels.requestPanel

import com.google.gson.Gson
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBIntSpinner
import com.ronnie.toastjet.engine.apiEngine.rest.invokeRestApi
import com.ronnie.toastjet.engine.scriptExecutor.ScriptExecutor
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Cursor
import java.awt.Dimension
import javax.swing.*

class RequestOptionsComponent(private val store: RequestStore, private val configStore: ConfigStore) : JPanel() {


    val theme = EditorColorsManager.getInstance().globalScheme
    val gson = Gson()

    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        minimumSize = Dimension(0, 35)
        preferredSize = Dimension(0, 35)
        maximumSize = Dimension(Int.MAX_VALUE, 35)


        val requestType = ComboBox(HttpMethod.entries.toTypedArray()).apply {
            selectedItem = "GET"
            maximumSize = Dimension(100, preferredSize.height)
            addActionListener {
                store.state.setState {
                    it.method = HttpMethod.valueOf(selectedItem as String)
                    it
                }
            }
        }

        add(requestType)
        add(Box.createHorizontalStrut(10))
        add(JLabel("Timeout:"))
        add(JBIntSpinner(30, 1, 10000, 1))
        add(Box.createHorizontalStrut(10))
        add(JComboBox(arrayOf("s", "ms", "mins")).apply {
            selectedItem = "s"
            maximumSize = Dimension(100, preferredSize.height)
        })
        add(Box.createHorizontalGlue())

        val protocolComboBox = ComboBox(arrayOf("HTTP/1.1", "HTTP/2", "HTTP/3")).apply {
            maximumSize = Dimension(100, preferredSize.height)
            selectedItem = "HTTP/2"
        }

        add(protocolComboBox)
        add(JButton("Send").apply {
            background = theme.defaultBackground
            foreground = theme.defaultForeground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mouseClicked = {
                        val request = ScriptExecutor.executeJsCode(
                            gson.toJson(
                                mapOf(
                                    Pair("config", configStore.state.getState()),
                                    Pair("api", store.state.getState())
                                )
                            )
                        )
                        val response = invokeRestApi(request)
                        println("The result of invoked api is $response")
                    },
                )
            )
        })

        add(Box.createHorizontalStrut(10))
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }
}