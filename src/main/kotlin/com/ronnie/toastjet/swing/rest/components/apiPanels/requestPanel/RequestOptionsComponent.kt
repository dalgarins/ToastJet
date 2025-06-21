package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBIntSpinner
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Cursor
import java.awt.Dimension
import javax.swing.*

class RequestOptionsComponent(private val store: RequestStore, private val configStore: ConfigStore) : JPanel() {


    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        sendButton.background = background
        sendButton.foreground = theme.globalScheme.defaultForeground
    }

    val sendButton = JButton("Send").apply {
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        addMouseListener(
            SwingMouseListener(
                mouseClicked = {
                    if (!store.response.getState().isBeingInvoked) {
                        store.response.setState {
                            it.isBeingInvoked = true
                            it.invoked = false
                            it
                        }
                    }
                },
            )
        )
    }

    val protocolComboBox = ComboBox(arrayOf("HTTP/1.1", "HTTP/2", "HTTP/3")).apply {
        maximumSize = Dimension(100, preferredSize.height)
        selectedItem = "HTTP/2"
    }

    val requestType = ComboBox(HttpMethod.entries.toTypedArray()).apply {
        selectedItem = "GET"
        maximumSize = Dimension(100, preferredSize.height)
        addActionListener {
            store.methodState.setState(HttpMethod.valueOf(selectedItem as String))
        }
    }

    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        minimumSize = Dimension(0, 35)
        preferredSize = Dimension(0, 35)
        maximumSize = Dimension(Int.MAX_VALUE, 35)
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
        add(protocolComboBox)
        add(sendButton)
        add(Box.createHorizontalStrut(10))
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }
}