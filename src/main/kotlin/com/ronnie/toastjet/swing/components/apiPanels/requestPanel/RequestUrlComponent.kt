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

    private fun getTextArea(url: String): LanguageTextField {
        return LanguageTextField(
            PlainTextLanguage.INSTANCE,
            store.appStore.project,
            url,
            true
        ).apply {
            font = Font("Sans", Font.PLAIN, 16)
            border = JBUI.Borders.empty(5, 10)
            document.addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    urlState.setState(text)
                }
            })
        }
    }

    private var textArea = getTextArea(urlState.getState())


    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        minimumSize = Dimension(0, 45)
        preferredSize = Dimension(0, 45)
        maximumSize = Dimension(Int.MAX_VALUE, 45)


        add(Box.createHorizontalStrut(10))
        add(JLabel("URL:").apply {
            font = Font(font.name, Font.BOLD, 18)
        })
        add(Box.createHorizontalStrut(10))
        textArea.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent?) = println("Focus GAINED")
            override fun focusLost(e: FocusEvent?) = println("Focus LOST")
        })

        store.paramsState.addEffect{
            try {
                val baseUrl = urlState.getState().split("?").first()
                val checkedParams = it.filter { p -> p.isChecked && p.key.isNotBlank() }

                val queryString = checkedParams.joinToString("&") { p ->
                    "${if (p.key.startsWith("{{")) p.key else URLEncoder.encode(p.key, "UTF-8")}=" +
                            "${if (p.value.startsWith("{{")) p.value else URLEncoder.encode(p.value, "UTF-8")}"
                }

                val finalUrl = if (queryString.isNotEmpty()) "$baseUrl?$queryString" else baseUrl
                if (textArea.document.text != finalUrl) {
                    textArea.setText(finalUrl)
                }
                store.urlState.setState(finalUrl)
            } catch (err: URISyntaxException) {
                println("There was URI syntax exception in the given code: $err")
            } catch (err: NoSuchElementException) {
                println("There was error splitting the url $err")
            }
        }


        add(textArea)
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }
}