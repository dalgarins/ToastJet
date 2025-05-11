package com.ronnie.toastjet.swing.components.apiPanels.requestPanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import java.awt.Font
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel


class RequestUrlComponent(val store: RequestStore) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        minimumSize = Dimension(0, 45)
        preferredSize = Dimension(0, 45)
        maximumSize = Dimension(Int.MAX_VALUE, 45)

        val urlState = store.state

        add(Box.createHorizontalStrut(10))
        add(JLabel("URL:").apply {
            font = Font(font.name, Font.BOLD, 18)
        })
        add(Box.createHorizontalStrut(10))

        val textArea =
            LanguageTextField(PlainTextLanguage.INSTANCE, store.appStore.project, urlState.getState().url, true).apply {
                font = Font("Sans", Font.PLAIN, 16)
                border = JBUI.Borders.empty(5, 10)
                document.addDocumentListener(object : DocumentListener {
                    override fun documentChanged(event: DocumentEvent) {
                        urlState.setState {
                            it.url = text
                            it
                        }
                    }
                })
            }

        add(textArea)
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }
}