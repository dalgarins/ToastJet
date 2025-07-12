package com.ronnie.toastjet.swing.socket.request

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.SocketMessage
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class MessageReqPanel(private val store: SocketStore) : JPanel(BorderLayout()) {

    private val messageListPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        border = JBUI.Borders.emptyRight(5)
    }

    private val newMessageButton = JButton("New Message").apply {
        addActionListener {
            store.messageList.setState {
                it.add(SocketMessage(title = "Message ${it.size + 1}", message = ""))
                updateMessageListUI()
                it
            }
        }
    }

    private val messageAddListPanel = JPanel(BorderLayout()).apply {
        add(messageListPanel, BorderLayout.CENTER)
        add(newMessageButton, BorderLayout.SOUTH)
        border = JBUI.Borders.emptyRight(5)
    }

    private val contentPanel = MessageContent(store, store.appStore)

    private val messageRadioButtons: MutableList<JBRadioButton> = mutableListOf()
    private val messageTitleFields: MutableList<JTextField> = mutableListOf()

    init {
        setupUI()
        setupListeners()
        updateMessageListUI()
        applyTheme(store.theme.getState())
    }

    private fun setupUI() {
        add(JBScrollPane(messageAddListPanel), BorderLayout.WEST)
        add(contentPanel, BorderLayout.CENTER)
    }

    private fun setupListeners() {
        store.theme.addListener(this::applyTheme)
        store.selectedMessage.addListener { selectedIndex ->
            updateSelectionInUI(selectedIndex)
        }
        store.messageList.addListener {
            updateMessageListUI()
        }
    }

    private fun updateSelectionInUI(selectedIndex: Int) {
        val previousSelectedIndex = messageRadioButtons.indexOfFirst { it.isSelected }
        if (previousSelectedIndex != -1 && previousSelectedIndex < messageRadioButtons.size) {
            messageRadioButtons[previousSelectedIndex].isSelected = false
            getMessagePanel(previousSelectedIndex)?.background = store.theme.getState().globalScheme.defaultBackground
        }

        if (selectedIndex >= 0 && selectedIndex < messageRadioButtons.size) {
            messageRadioButtons[selectedIndex].isSelected = true
            getMessagePanel(selectedIndex)?.background = JBColor.background()
        }

        messageTitleFields.forEachIndexed { index, textField ->
            textField.background = if (index == selectedIndex) JBColor.background()
            else store.theme.getState().globalScheme.defaultBackground
        }

        applyTheme(store.theme.getState())
    }

    private fun updateMessageListUI() {
        messageListPanel.removeAll()
        messageRadioButtons.clear()
        messageTitleFields.clear()

        store.messageList.getState().forEachIndexed { index, message ->
            val messagePanel = createMessageItemPanel(index, message)
            messageListPanel.add(messagePanel)
        }

        messageListPanel.revalidate()
        messageListPanel.repaint()

        updateSelectionInUI(store.selectedMessage.getState())
    }

    private fun createMessageItemPanel(index: Int, message: SocketMessage): JPanel {
        val titleField = JTextField().apply {
            text = message.title
            preferredSize = Dimension(80, 24)
            maximumSize = preferredSize
            background = store.theme.getState().globalScheme.defaultBackground
            border = JBUI.Borders.empty()
            addFocusListener(object : FocusAdapter() {
                override fun focusLost(e: FocusEvent?) {
                    isEditable = false
                    store.messageList.setState { currentList ->
                        currentList[index].title = text
                        currentList
                    }
                }
            })
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (e?.clickCount == 2) { // Double click to edit
                        isEditable = true
                        requestFocusInWindow()
                    }
                }
            })
        }
        messageTitleFields.add(titleField)

        val radioButton = JBRadioButton().apply {
            isSelected = store.selectedMessage.getState() == index
            isOpaque = false
            addActionListener {
                messageRadioButtons.forEachIndexed { i, _ ->
                    if (i != index) messageRadioButtons[i].isSelected = false
                }
                store.selectedMessage.setState(index)
            }
        }
        messageRadioButtons.add(radioButton)

        val deleteLabel = JLabel().apply {
            icon = IconUtil.colorize(AllIcons.Actions.DeleteTag, JBColor.RED)
            toolTipText = "Delete Message"
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    store.messageList.setState {
                        if (index < it.size) { // Ensure index is valid
                            it.removeAt(index)
                        }
                        // Adjust selected message if the deleted one was selected or subsequent
                        if (store.selectedMessage.getState() == index) {
                            store.selectedMessage.setState(0) // Select first if deleted
                        } else if (store.selectedMessage.getState() > index) {
                            store.selectedMessage.setState(store.selectedMessage.getState() - 1)
                        }
                        updateMessageListUI() // Rebuild UI after deletion
                        it
                    }
                }
            })
        }

        val messagePanel = JPanel(BorderLayout()).apply {
            border = JBUI.Borders.empty(4, 8)
            background = store.theme.getState().globalScheme.defaultBackground // Initial background
            preferredSize = Dimension(200, 32)
            maximumSize = preferredSize
            add(radioButton, BorderLayout.WEST)
            add(JBScrollPane(titleField).apply {
                border = JBUI.Borders.empty() // Remove scroll pane border
            }, BorderLayout.CENTER)
            add(deleteLabel, BorderLayout.EAST)

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (e?.source != titleField || e.clickCount != 2) {
                        store.selectedMessage.setState(index)
                    }
                }
            })
        }
        return messagePanel
    }

    private fun applyTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        messageListPanel.background = background
        messageAddListPanel.background = background
        contentPanel.background = background

        store.messageList.getState().forEachIndexed { index, _ ->
            getMessagePanel(index)?.background = if (store.selectedMessage.getState() == index) JBColor.background()
            else theme.globalScheme.defaultBackground

            messageTitleFields.getOrNull(index)?.background =
                if (store.selectedMessage.getState() == index) JBColor.background()
                else theme.globalScheme.defaultBackground

            messageRadioButtons.getOrNull(index)?.background = theme.globalScheme.defaultBackground
        }
    }

    private fun getMessagePanel(index: Int): JPanel? {
        return messageListPanel.getComponent(index) as? JPanel
    }
}