package com.ronnie.toastjet.swing.socket.request

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.ronnie.toastjet.model.enums.EditorContentType
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody.HTMLEditor
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody.JsonEditor
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody.MarkdownEditor
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody.TextEditor
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody.XMLEditor
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody.YamlEditor
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel


class MessageContent(
    val store: SocketStore, val appStore: AppStore
) : JPanel(BorderLayout()) {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
        topPanel.background = background
        leftPanel.background = background
        rightPanel.background = background
    }

    var panel: JComponent? = null

    val leftPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
        add(ComboBox(EditorContentType.entries.toTypedArray()).apply {
            addActionListener {
                store.contentType.setState(selectedItem as EditorContentType)
            }
        })
    }
    val rightPanel = JPanel(FlowLayout(FlowLayout.RIGHT)).apply {
        add(JButton("Send  \uD83D\uDE80"))
    }

    val topPanel = JPanel(BorderLayout()).apply {
        border = BorderFactory.createEmptyBorder(2, 10, 2, 10)

        add(leftPanel, BorderLayout.LINE_START)
        add(rightPanel, BorderLayout.LINE_END)
    }

    init {
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        handleContentTypeChange(store.contentType.getState())
        store.contentType.addListener(this::handleContentTypeChange)
        add(topPanel, BorderLayout.NORTH)
    }

    fun handleContentTypeChange(editorType: EditorContentType) {
        if(panel != null) remove(panel)
        when (editorType) {
            EditorContentType.JSON -> {
                panel = JsonEditor(store.content, appStore)
            }

            EditorContentType.XML -> {
                panel = XMLEditor(store.content, appStore)
            }

            EditorContentType.MD -> {
                panel = MarkdownEditor(store.content, appStore)
            }

            EditorContentType.HTML -> {
                panel = HTMLEditor(store.content, appStore)
            }

            EditorContentType.YAML -> {
                panel = YamlEditor(store.content, appStore)
            }

            else -> {
                panel = TextEditor(store.content, appStore)
            }
        }
        add(panel!!, BorderLayout.CENTER)
        repaint()
        revalidate()
    }

}