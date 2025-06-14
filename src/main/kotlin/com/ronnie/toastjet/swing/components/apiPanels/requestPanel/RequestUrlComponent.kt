package com.ronnie.toastjet.swing.components.apiPanels.requestPanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import java.awt.Font
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.net.URISyntaxException
import java.net.URLEncoder
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class RequestUrlComponent(val store: RequestStore) : JPanel() {

    private val urlState = store.urlState

    // Lazy initialization of text area
    private val textArea: LanguageTextField by lazy {
        createTextArea(urlState.getState())
    }

    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        isOpaque = true
        minimumSize = Dimension(0, JBUI.scale(30))
        preferredSize = Dimension(0, JBUI.scale(35))
        maximumSize = Dimension(Int.MAX_VALUE, JBUI.scale(30))

        add(Box.createHorizontalStrut(JBUI.scale(10)))

        add(JLabel("URL:").apply {
            font = JBUI.Fonts.label().deriveFont(Font.BOLD, 16f)
            foreground = store.theme.getState().globalScheme.defaultForeground
        })

        add(Box.createHorizontalStrut(JBUI.scale(10)))

        // Add the text area
        add(textArea, Box.LEFT_ALIGNMENT)

        // Theme listener
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)

        // Listen to params changes
        store.paramsState.addEffect {
            try {
                val baseUrl = urlState.getState().split("?").first()
                val checkedParams = it.filter { p -> p.isChecked && p.key.isNotBlank() }

                val queryString = checkedParams.joinToString("&") { p ->
                    "${if (p.key.startsWith("{{")) p.key else URLEncoder.encode(p.key, "UTF-8")}=" +
                            "${if (p.value.startsWith("{{")) p.value else URLEncoder.encode(p.value, "UTF-8")}"
                }

                val finalUrl = if (queryString.isNotEmpty()) "$baseUrl?$queryString" else baseUrl
                if (textArea.text != finalUrl) {
                    textArea.text = finalUrl
                }
                urlState.setEffect(finalUrl)
            } catch (err: URISyntaxException) {
                println("URI syntax error: $err")
            } catch (err: NoSuchElementException) {
                println("Error splitting URL: $err")
            }
        }
    }

    private fun createTextArea(initialText: String): LanguageTextField {
        return LanguageTextField(
            PlainTextLanguage.INSTANCE,
            store.appStore.project,
            initialText,
            true
        ).apply {
            font = JBFont.h3()
            isFocusable = true

            border = JBUI.Borders.empty(0,10)
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground

            // Update colors when theme changes
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }

            // Handle input changes
            document.addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    urlState.setState(text)
                }
            })

            // Optional: Simulate placeholder text on focus loss
            addFocusListener(object : FocusAdapter() {
                override fun focusGained(e: FocusEvent?) {
                    if (text.isEmpty()) {
                        text = ""
                    }
                }

                override fun focusLost(e: FocusEvent?) {

                }
            })
        }
    }

    fun setTheme(themeManager: EditorColorsManager) {
        background = themeManager.globalScheme.defaultBackground
        foreground = themeManager.globalScheme.defaultForeground
    }
}