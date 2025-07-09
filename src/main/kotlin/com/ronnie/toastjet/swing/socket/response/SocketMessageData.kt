package com.ronnie.toastjet.swing.socket.response

import com.intellij.lang.Language
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.ui.JBColor
import com.intellij.ui.LanguageTextField
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.JBFont
import com.ronnie.toastjet.model.enums.ContentType
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.detectFormat
import com.ronnie.toastjet.swing.store.SocketStore
import com.ronnie.toastjet.swing.widgets.RadioTabbedPanel
import com.ronnie.toastjet.swing.widgets.TabbedAction
import java.awt.BorderLayout
import java.awt.GridBagLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import javax.swing.JPanel
import javax.swing.JLabel

class SocketMessageDataPanel(
    val store: SocketStore,
) : JPanel(BorderLayout()), Disposable {

    private var currentContentType: ContentType = ContentType.PLAIN_TEXT

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
        tabbedPane.background = background
        tabbedPane.foreground = foreground
    }

    val centerPanel = JPanel(GridBagLayout()).apply {
        background = store.theme.getState().globalScheme.defaultBackground
        store.theme.addListener {
            background = it.globalScheme.defaultBackground
        }
        add(JLabel("No messages have been exchanged").apply {
            foreground = JBColor.RED
            font = JBFont.h2()
        })
    }

    private val tabbedPane = RadioTabbedPanel(
        theme = store.theme,
        tabs = mutableListOf(),
        action = mutableListOf(
            TabbedAction("Wrap") {
                val selectedEditor = when (it) {
                    0 -> originalEditor.editor
                    1 -> formattedEditor.editor
                    else -> return@TabbedAction
                }
                selectedEditor?.settings?.isUseSoftWraps?.let { isWrapped ->
                    selectedEditor.settings.isUseSoftWraps = !isWrapped
                }
            },
            TabbedAction("Copy") {
                val selectedEditor = when (it) {
                    0 -> originalEditor.editor
                    1 -> formattedEditor.editor
                    else -> return@TabbedAction
                }
                val content = selectedEditor?.document?.text
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(content), null)
                Messages.showInfoMessage("Content copied to clipboard", "Copied")
            }
        )
    )

    private lateinit var originalEditor: LanguageTextField
    private lateinit var formattedEditor: LanguageTextField

    init {
        val messages = store.messagesState.getState()
        val selectedIndex = store.selectedResMessage.getState()
        val msg = messages.getOrNull(selectedIndex)?.message

        if (msg == null) {
            if (messages.isNotEmpty()) {
                val lastIndex = messages.lastIndex
                store.selectedResMessage.setState(lastIndex)
                val lastMsg = messages[lastIndex].message
                updateUI(lastMsg)
            } else {
                add(centerPanel)
            }
        } else {
            updateUI(msg)
        }

        store.messagesState.addEffect { response ->
            val msg = response.getOrNull(store.selectedResMessage.getState())
            if (msg != null) {
                updateUI(msg.message)
            }
        }

        store.selectedResMessage.addEffect { index ->
            val msg = store.messagesState.getState().getOrNull(index)
            if (msg != null) {
                updateUI(msg.message)
            }
        }

        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }

    private fun createReadOnlyEditor(content: String, lang: Language?): LanguageTextField {
        val project = store.appStore.project
        val language = lang ?: PlainTextLanguage.INSTANCE

        return LanguageTextField(language, project, content, false).apply {
            isViewer = true
            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
        }
    }

    private fun updateUI(content: String) {
        removeAll()

        val contentType = detectFormat(content)
        this.currentContentType = contentType

        val lang = contentType.value

        originalEditor = createReadOnlyEditor(content, lang)
        formattedEditor = createAndFormatEditor(content, lang)

        tabbedPane.removeAll()
        tabbedPane.addTab("Original", originalEditor)
        tabbedPane.addTab("Formatted", formattedEditor)

        if (contentType == ContentType.HTML) {
            val browser = JBCefBrowser()
            Disposer.register(this, browser)
            browser.loadHTML(formattedEditor.text)
            tabbedPane.addTab("Preview", browser.component)
        }

        add(tabbedPane, BorderLayout.CENTER)
        revalidate()
        repaint()
    }
    private fun createAndFormatEditor(content: String, lang: Language?): LanguageTextField {
        val project = store.appStore.project
        val language = lang ?: PlainTextLanguage.INSTANCE

        val textField = LanguageTextField(language, project, content, false).apply {
            isViewer = true
            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
        }

        // Set initial content immediately
        ApplicationManager.getApplication().invokeLater {
            ApplicationManager.getApplication().runWriteAction {
                textField.document.setText(content)
            }
        }

        // Run reformatting on a background thread
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                WriteCommandAction.runWriteCommandAction(project) {
                    val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(textField.document) ?: run {
                        return@runWriteCommandAction
                    }


                    val formattedText = CodeStyleManager.getInstance(project).reformat(psiFile).text

                    // Update the UI on EDT
                    ApplicationManager.getApplication().invokeLater {
                        ApplicationManager.getApplication().runWriteAction {
                            textField.document.setText(formattedText)
                        }
                    }
                }
            } catch (e: Exception) {
                ApplicationManager.getApplication().invokeLater {
                    Messages.showErrorDialog("Formatting failed: ${e.message}", "Format Error")
                }
            }
        }

        return textField
    }

    override fun dispose() {
        // Clean up resources if needed
    }
}