package com.ronnie.toastjet.swing.socket.request

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class MessageReqPanel(val store: SocketStore) : JPanel(BorderLayout()) {
    val messageList = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = JBUI.Borders.customLineRight(JBColor.LIGHT_GRAY)
    }

    val messageComponents = mutableListOf<JTextArea>()
    val messagePanelList = mutableListOf<JPanel>()


    fun createMessageList(): JPanel {
        messageList.removeAll()

        store.messageList.getState().forEachIndexed { index, message ->
            val textArea = JTextArea().apply {
                text = message.title
                isEditable = false
                lineWrap = false
                wrapStyleWord = true
                maximumSize = preferredSize
                border = JBUI.Borders.empty(5)
            }

            val editButton = JLabel("\u270E").apply {
                preferredSize = Dimension(20, 30)
                isFocusable = false
            }

            val messagePanel = JPanel(BorderLayout()).apply {
                border = JBUI.Borders.empty(5)
                preferredSize = Dimension(200, 30)
                maximumSize = preferredSize
                add(editButton, BorderLayout.WEST)
                add(JBScrollPane(textArea), BorderLayout.CENTER)
            }
            messagePanelList.add(messagePanel)

            messagePanel.addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: java.awt.event.MouseEvent?) {
                    messagePanelList[store.selectedMessage.getState()].background =
                        store.theme.getState().globalScheme.defaultBackground
                    store.selectedMessage.setState(index)
                    messagePanelList[index].background = JBColor.LIGHT_GRAY
                    textArea.requestFocusInWindow()
                }
            })

            editButton.addMouseListener(
                SwingMouseListener(
                    mouseClicked = {
                        textArea.isEditable = true
                        textArea.requestFocusInWindow()
                    }
                ))

            textArea.addFocusListener(object : java.awt.event.FocusAdapter() {
                override fun focusLost(e: java.awt.event.FocusEvent?) {
                    textArea.isEditable = false
                    store.messageList.setState {
                        it[index].title = textArea.text
                        it
                    }
                }
            })

            messageList.add(messagePanel)
            messageComponents.add(textArea)
        }

        return messageList
    }

    val content = MessageContent(store, store.appStore)

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        messageList.background = background
        content.background = background
        messageComponents.forEach { it.background = background }
        messageList.components.forEach {
            it.background = background
        }
        messagePanelList[store.selectedMessage.getState()].background = JBColor.background()
        messageComponents[store.selectedMessage.getState()].background = JBColor.background()
    }

    init {
        add(messageList, BorderLayout.WEST)
        add(content, BorderLayout.CENTER)
        createMessageList()
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }
}