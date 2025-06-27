package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData

import com.google.gson.JsonParser
import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.IncorrectOperationException
import com.ronnie.toastjet.model.data.ResponseData
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.StateHolder
import com.ronnie.toastjet.swing.widgets.RadioTabbedPanel
import com.ronnie.toastjet.swing.widgets.TabbedAction
import org.xml.sax.helpers.DefaultHandler
import org.yaml.snakeyaml.Yaml
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.StringReader
import java.nio.charset.StandardCharsets
import javax.swing.JPanel
import javax.xml.parsers.SAXParserFactory

enum class ContentType(val value: Language) {
    JSON(JsonLanguage.INSTANCE),
    XML(XMLLanguage.INSTANCE),
    HTML(HTMLLanguage.INSTANCE),
    YAML(Language.findLanguageByID("yaml") ?: PlainTextLanguage.INSTANCE),
    JAVASCRIPT(Language.findLanguageByID("javascript") ?: PlainTextLanguage.INSTANCE),
    PLAIN_TEXT(PlainTextLanguage.INSTANCE)
}

fun detectFormat(content: String): ContentType {
    val trimmed = content.trim()

    return when {
        trimmed.startsWith("{") && trimmed.endsWith("}") -> tryJson(trimmed)
        trimmed.startsWith("[") && trimmed.endsWith("]") -> tryJson(trimmed)
        trimmed.startsWith("<") && trimmed.endsWith(">") -> tryXml(trimmed)
        trimmed.startsWith("---") || trimmed.contains("yaml") -> tryYaml(trimmed)
        trimmed.startsWith("<!DOCTYPE html") || trimmed.startsWith("<html") -> ContentType.HTML
        trimmed.startsWith("<!--") || trimmed.startsWith("//") || trimmed.contains("function") || trimmed.contains("var ") -> ContentType.JAVASCRIPT
        else -> ContentType.PLAIN_TEXT
    }
}

private fun tryJson(content: String): ContentType {
    return try {
        JsonParser.parseString(content)
        ContentType.JSON
    } catch (_: Exception) {
        println("The response is not json")
        ContentType.PLAIN_TEXT
    }
}

private fun tryXml(content: String): ContentType {
    return try {
        val factory = SAXParserFactory.newInstance()
        factory.newSAXParser().parse(content.byteInputStream(StandardCharsets.UTF_8), DefaultHandler())
        ContentType.XML
    } catch (_: Exception) {
        println("The response is not xml")
        ContentType.PLAIN_TEXT
    }
}

private fun tryYaml(content: String): ContentType {
    return try {
        Yaml().parse(StringReader(content))
        ContentType.YAML
    } catch (_: Exception) {
        println("The response is not yaml")
        ContentType.PLAIN_TEXT
    }
}

class ResponseBodyPanel(
    val theme: StateHolder<EditorColorsManager>,
    val response : StateHolder<out ResponseData>,
    val appStore: AppStore
) : JPanel(BorderLayout()), Disposable {

    private var currentContentType: ContentType = ContentType.PLAIN_TEXT

    fun setTheme(theme: EditorColorsManager){
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
        tabbedPane.background = background
        tabbedPane.foreground = foreground
    }

    private val tabbedPane = RadioTabbedPanel(
        theme = theme,
        tabs = mutableListOf(),
        action = mutableListOf(
            TabbedAction("Wrap") {
                val wrapState = !originalEditor.settings.isUseSoftWraps
                originalEditor.settings.isUseSoftWraps = wrapState
                formattedEditor.settings.isUseSoftWraps = wrapState
            },
            TabbedAction("Copy") {
                val selectedEditor = when (it) {
                    0 -> originalEditor
                    1 -> formattedEditor
                    else -> return@TabbedAction
                }
                val content = selectedEditor.document.text
                val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                clipboard.setContents(StringSelection(content), null)
                Messages.showInfoMessage("Content copied to clipboard", "Copied")
            }
        )
    )
    private lateinit var originalEditor: Editor
    private lateinit var formattedEditor: Editor

    private val controlPanel = JPanel(FlowLayout(FlowLayout.RIGHT))

    private var currentEditor: Editor? = null

    init {
        controlPanel.isOpaque = false
        add(tabbedPane)
        updateUI(response.getState().data ?: "", response.getState().responseHeaders)
        response.addEffect { response ->
            updateUI(response.data ?: "", response.responseHeaders)
        }
        setTheme(theme.getState())
        theme.addListener(this::setTheme)
    }

    private fun createReadOnlyEditor(content: String, lang: Language?): Editor {
        val project = appStore.project
        val language = lang ?: PlainTextLanguage.INSTANCE
        val virtualFile = LightVirtualFile("temp", language, content)
        val document = EditorFactory.getInstance().createDocument(content)
        val editorFactory = EditorFactory.getInstance()
        val editor = editorFactory.createEditor(document, project, virtualFile.fileType, true)
        return editor
    }


    private fun updateUI(content: String, headers: Map<String, String>) {
        val contentType = guessContentType(headers, content)

        this.currentContentType = contentType

        originalEditor = createReadOnlyEditor(content, contentType.value)
        formattedEditor = setPrettyPrinted(content, contentType.value)

        // Clear tabs
        tabbedPane.removeAll()

        tabbedPane.addTab("Original", originalEditor.component)
        tabbedPane.addTab("Formatted", formattedEditor.component)

        if (contentType == ContentType.HTML) {
            val browser = JBCefBrowser()
            Disposer.register(this) { Disposer.dispose(browser) }

            val htmlContent = formattedEditor.document.text
            browser.loadHTML(htmlContent)
            tabbedPane.addTab("Preview", browser.component)
        }

        revalidate()
        repaint()
    }

    override fun dispose() {
        //To dispose is used by the editor component
    }

    private fun guessContentType(headers: Map<String, String>, content: String): ContentType {
        val contentTypeHeader = headers.entries
            .find { it.key.equals("Content-Type", ignoreCase = true) }
            ?.value?.lowercase()

        return when {
            contentTypeHeader?.contains("json") == true -> ContentType.JSON
            contentTypeHeader?.contains("xml") == true -> ContentType.XML
            contentTypeHeader?.contains("html") == true -> ContentType.HTML
            contentTypeHeader?.contains("yaml") == true -> ContentType.YAML
            contentTypeHeader?.contains("javascript") == true -> ContentType.JAVASCRIPT
            else -> detectFormat(content)
        }
    }

    private fun setPrettyPrinted(content: String, lang: Language?): Editor {
        val project = appStore.project
        val language = lang ?: PlainTextLanguage.INSTANCE
        val extension = language.associatedFileType?.defaultExtension ?: "txt"
        val virtualFile = LightVirtualFile("temp.$extension", language, content)
        val document = FileDocumentManager.getInstance().getDocument(virtualFile)
            ?: EditorFactory.getInstance().createDocument(content)
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
            ?: throw IllegalStateException("Failed to create PsiFile")
        reformatPsi(psiFile, project) { formatted ->
            ApplicationManager.getApplication().invokeLater {
                document.setText(formatted)
            }
        }
        val editorFactory = EditorFactory.getInstance()
        val editor = editorFactory.createEditor(document, project, virtualFile.fileType, true)
        editor.settings.isLineNumbersShown = true
        editor.settings.isFoldingOutlineShown = true
        editor.settings.isUseSoftWraps = true
        return editor
    }

    private fun reformatPsi(psiFile: PsiFile, project: Project, onSuccess: (String) -> Unit) {
        ApplicationManager.getApplication().executeOnPooledThread {
            try {
                WriteCommandAction.runWriteCommandAction(project) {
                    CodeStyleManager.getInstance(project).reformat(psiFile)
                    onSuccess(psiFile.text)
                }
            } catch (e: IncorrectOperationException) {
                e.printStackTrace()
                ApplicationManager.getApplication().invokeLater {
                    Messages.showErrorDialog("Formatting failed: ${e.message}", "Format Error")
                }
            }
        }
    }

    override fun removeNotify() {
        super.removeNotify()
        currentEditor?.let {
            EditorFactory.getInstance().releaseEditor(it)
            currentEditor = null
        }
    }
}


