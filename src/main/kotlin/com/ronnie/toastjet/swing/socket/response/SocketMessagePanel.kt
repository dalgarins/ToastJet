package com.ronnie.toastjet.swing.socket.response

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.SocketMessageData
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class SocketMessagePanel(val store: SocketStore) : JPanel(BorderLayout()) {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        leftPanel.background = background
        rightPanel.background = background
    }

    val leftPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = JBUI.Borders.customLineRight(JBColor.LIGHT_GRAY)
    }

    val rightPanel = SocketMessageDataPanel(store)

    init {
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        add(leftPanel, BorderLayout.WEST)
        add(rightPanel, BorderLayout.CENTER)
        store.messagesState.addListener(this::populateLeftPanel)
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }

    fun populateLeftPanel(messageList: List<SocketMessageData>) {
        leftPanel.removeAll()
        messageList.forEachIndexed { index, it ->
            val messageLabel = JLabel(if (it.message.length > 50) it.message.substring(0, 47) + "..." else it.message)

            val deleteIcon = JLabel("X   ").apply {
                foreground = JBColor.RED
            }

            val sendReceivePanel = JLabel(if (it.send) "↑" else "↓").apply {
                foreground = if (it.send) JBColor.GREEN else JBColor.BLUE
                font = Font(font.name, Font.BOLD, 20)
                addMouseListener(
                    SwingMouseListener(
                        mouseClicked = {
                            store.selectedResMessage.setState(index)
                        }
                    ))
            }

            val messagePanel = JPanel(BorderLayout()).apply {
                border = JBUI.Borders.empty(5)
                preferredSize = Dimension(200, 30)
                background =
                    if (store.selectedResMessage.getState() == index) JBColor.LIGHT_GRAY else store.theme.getState().globalScheme.defaultBackground
                store.selectedResMessage.addListener {
                    background =
                        if (store.selectedResMessage.getState() == index) JBColor.LIGHT_GRAY else store.theme.getState().globalScheme.defaultBackground
                }

                store.theme.addListener {
                    background =
                        if (store.selectedResMessage.getState() == index) JBColor.LIGHT_GRAY else store.theme.getState().globalScheme.defaultBackground
                }

                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(
                    SwingMouseListener(
                    mouseClicked = {
                        store.selectedResMessage.setState(index)
                    }
                ))
                maximumSize = preferredSize
                add(sendReceivePanel, BorderLayout.EAST)
                add(deleteIcon, BorderLayout.WEST)
                add(messageLabel, BorderLayout.CENTER)
            }

            leftPanel.add(messagePanel)
        }
    }

}