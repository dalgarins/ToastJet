package com.ronnie.toastjet.swing.socket.request

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBIntSpinner
import com.ronnie.toastjet.engine.apiEngine.socket.SocketClient
import com.ronnie.toastjet.model.enums.SocketType
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.Cursor
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel

class SocketOptionsComponent(
    val store: SocketStore, val configStore: ConfigStore
) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        connectButton.background = background
        connectButton.foreground = theme.globalScheme.defaultForeground
    }

    val connectButton = JButton("Connect").apply {
        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        addMouseListener(
            SwingMouseListener(
                mouseClicked = {
                    if (store.socketConnected.getState()) {
                        SocketClient.disconnect(store)
                    } else {
                        SocketClient.connect(store)
                    }
                },
            )
        )
    }

    val protocolComboBox = ComboBox(arrayOf("HTTP/1.1", "HTTP/2", "HTTP/3")).apply {
        maximumSize = Dimension(100, preferredSize.height)
        selectedItem = "HTTP/2"
    }

    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        minimumSize = Dimension(0, 35)
        preferredSize = Dimension(0, 35)
        maximumSize = Dimension(Int.MAX_VALUE, 35)
        add(Box.createHorizontalStrut(10))
        add(JLabel("Socket Type"))
        add(Box.createHorizontalStrut(10))
        add(JComboBox(SocketType.entries.toTypedArray()).apply {
            selectedItem = SocketType.WebSocket
            maximumSize = Dimension(100, preferredSize.height)
            addActionListener {
                store.socketState.setState(this.selectedItem as SocketType)
            }
        })
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
        add(connectButton)
        add(Box.createHorizontalStrut(10))
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        store.socketConnected.addListener {
            connectButton.text = if (it) "Disconnect" else "Connect"
        }
    }
}