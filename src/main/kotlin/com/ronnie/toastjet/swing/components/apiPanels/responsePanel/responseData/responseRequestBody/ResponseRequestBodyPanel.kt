package com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData.responseRequestBody

import com.intellij.json.JsonLanguage
import com.intellij.lang.Language
import com.intellij.lang.html.HTMLLanguage
import com.intellij.lang.xml.XMLLanguage
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBFont
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.editor.TJsonLanguage
import com.ronnie.toastjet.model.data.GraphQLData
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.RawType
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants

class ResponseRequestBodyPanel(val store: RequestStore) : JPanel() {

    init {
        background = store.theme.getState().globalScheme.defaultBackground
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    private fun createReadOnlyEditor(content: String, lang: Language?): Editor {
        val project = store.appStore.project
        val language = lang ?: PlainTextLanguage.INSTANCE
        val virtualFile = LightVirtualFile("temp", language, content)
        val document = EditorFactory.getInstance().createDocument(content)
        document.setReadOnly(true)

        val editorFactory = EditorFactory.getInstance()
        val editor = editorFactory.createEditor(document, project, virtualFile.fileType, true)

        if (editor is EditorEx) {
            editor.isRendererMode = true
            editor.highlighter =
                EditorHighlighterFactory.getInstance().createEditorHighlighter(project, virtualFile)
        }

        return editor
    }

    private fun getRequestBodyType(): String {
        val bodyType = store.response.getState().apiRequestData.bodyTypeState
        val rawType = store.response.getState().apiRequestData.rawTypeState
        return if (bodyType == BodyType.RAW) rawType.name else bodyType.name
    }

    private fun getBodyComponent(): JComponent {
        val actualRequest = store.response.getState().apiRequestData
        val bodyType = actualRequest.bodyTypeState
        val rawType = actualRequest.rawTypeState

        return when (bodyType) {
            BodyType.None -> JPanel(BorderLayout()).apply {
                background = store.theme.getState().globalScheme.defaultBackground
                val label = JBLabel("No Request Body").apply {
                    font = JBFont.h2()
                    foreground = store.theme.getState().globalScheme.defaultForeground
                    horizontalAlignment = SwingConstants.CENTER
                }
                store.theme.addListener {
                    background = it.globalScheme.defaultBackground
                    label.foreground = it.globalScheme.defaultForeground
                }
                add(label, BorderLayout.NORTH)
            }

            BodyType.FormData -> {
                val data = HashMap<String, String>()
                actualRequest.formData.forEach {
                    if (it.enabled) {
                        data[it.key] = it.value
                    }
                }
                ConstructResReqBody(data, store.theme).apply {
                    background = theme.getState().globalScheme.defaultBackground
                    theme.addListener {
                        background = it.globalScheme.defaultBackground
                    }
                }
            }

            BodyType.URLEncoded -> {
                val data = HashMap<String, String>()
                actualRequest.urlEncoded.forEach {
                    if (it.isChecked) {
                        data[it.key] = it.value
                    }
                }
                ConstructResReqBody(data,store.theme).apply {
                    background = theme.getState().globalScheme.defaultBackground
                    theme.addListener {
                        background = it.globalScheme.defaultBackground
                    }
                }
            }

            BodyType.Binary -> JLabel("Binary Body").apply {
                background = store.theme.getState().globalScheme.defaultBackground
                store.theme.addListener {
                    background = it.globalScheme.defaultBackground
                }
                border = JBUI.Borders.empty(10)
            }

            BodyType.RAW -> when (rawType) {
                RawType.JSON -> {
                    createReadOnlyEditor(actualRequest.json, JsonLanguage.INSTANCE).component
                }

                RawType.XML -> {
                    createReadOnlyEditor(actualRequest.xml, XMLLanguage.INSTANCE).component
                }

                RawType.TEXT -> {
                    createReadOnlyEditor(actualRequest.text, PlainTextLanguage.INSTANCE).component
                }

                RawType.HTML -> {
                    createReadOnlyEditor(actualRequest.html, HTMLLanguage.INSTANCE).component
                }

                RawType.JS -> {
                    createReadOnlyEditor(
                        actualRequest.js,
                        Language.findLanguageByID("javascript") ?: PlainTextLanguage.INSTANCE
                    ).component
                }

                RawType.GraphQL -> {
                    ReadonlyGraphQLEditor(store, actualRequest.graphQl).apply {
                        background = theme.defaultBackground
                        foreground = theme.defaultForeground
                    }
                }
            }
        }
    }

    private fun addHeader(label: JComponent) {
        add(JPanel(BorderLayout()).apply {
            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
            border = JBUI.Borders.empty(10, 10, 5, 10)
            add(label, BorderLayout.WEST)
        })
    }

    private fun addContent(component: JComponent) {
        add(JPanel(BorderLayout()).apply {
            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
            add(component, BorderLayout.CENTER)
        })
    }

    override fun addNotify() {
        super.addNotify()

        removeAll()

        val typeLabel = JLabel("Body Type : ${getRequestBodyType()}").apply {
            foreground = JBColor.GRAY
        }

        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0)).apply {
            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
            add(typeLabel)
        }
        addHeader(headerPanel)
        addContent(getBodyComponent())

        revalidate()
        repaint()
    }
}

class ReadonlyGraphQLEditor(store: RequestStore, data: GraphQLData) : JPanel() {
    val theme = EditorColorsManager.getInstance().globalScheme

    private val topScrollPane = JBScrollPane(JPanel().apply {
        background = theme.defaultBackground
        foreground = theme.defaultForeground
        layout = BorderLayout()

        add(JLabel("Query"), BorderLayout.NORTH).apply {
            border = JBUI.Borders.compound(
                JBUI.Borders.emptyTop(10),
                JBUI.Borders.emptyBottom(10)
            )
        }

        add(JPanel().apply {
            layout = BorderLayout()
            border = JBUI.Borders.compound(
                JBUI.Borders.empty(10),
                JBUI.Borders.customLine(JBColor.LIGHT_GRAY, 1),
                JBUI.Borders.empty(5)
            )
            background = theme.defaultBackground
            val project = store.appStore.project

            val virtualFile = LightVirtualFile(
                "temp.query",
                Language.findLanguageByID("graphql") ?: PlainTextLanguage.INSTANCE,
                data.query
            )

            val document = EditorFactory.getInstance().createDocument(data.query)
            document.setReadOnly(true)

            val editor = EditorFactory.getInstance().createEditor(document, project)

            if (editor is EditorEx) {
                editor.isRendererMode = true // Ensures no blinking cursor or focusable UI
                editor.highlighter = EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(project, virtualFile)
            }

            add(editor.component, BorderLayout.CENTER)
        }, BorderLayout.CENTER)
    }).apply {
        preferredSize = Dimension(600, this.preferredHeight)
    }

    private val bottomScrollPane = JBScrollPane(JPanel().apply {
        background = theme.defaultBackground
        foreground = theme.defaultForeground
        layout = BorderLayout()

        add(JLabel("Variables"), BorderLayout.NORTH).apply {
            border = JBUI.Borders.compound(
                JBUI.Borders.emptyTop(10),
                JBUI.Borders.emptyBottom(10)
            )
        }

        add(JPanel().apply {
            layout = BorderLayout()
            border = JBUI.Borders.compound(
                JBUI.Borders.empty(10),
                JBUI.Borders.customLine(JBColor.LIGHT_GRAY, 1),
                JBUI.Borders.empty(5)
            )
            background = theme.defaultBackground
            val project = store.appStore.project

            val virtualFile = LightVirtualFile("temp.json", TJsonLanguage.INSTANCE, data.variable)

            val document = EditorFactory.getInstance().createDocument(data.variable)
            document.setReadOnly(true)

            val editor = EditorFactory.getInstance().createEditor(document, project)

            if (editor is EditorEx) {
                editor.isRendererMode = true
                editor.highlighter = EditorHighlighterFactory.getInstance()
                    .createEditorHighlighter(project, virtualFile)
            }

            add(editor.component, BorderLayout.CENTER)
        }, BorderLayout.CENTER)
    })

    private val splitter = OnePixelSplitter(false, 0.5f).apply {
        dividerWidth = 1
        divider.background = theme.defaultBackground
    }

    init {
        layout = BorderLayout()
        border = JBUI.Borders.empty()

        updateSplitterOrientation()
        add(splitter, BorderLayout.CENTER)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                updateSplitterOrientation()
            }
        })
    }

    private fun updateSplitterOrientation() {
        val isHorizontal = width <= 1200

        splitter.apply {
            if (orientation != isHorizontal) {
                orientation = isHorizontal
                firstComponent = topScrollPane
                secondComponent = bottomScrollPane
                proportion = 0.5f

                border = JBUI.Borders.compound(
                    if (isHorizontal) JBUI.Borders.emptyTop(25) else JBUI.Borders.empty(),
                    JBUI.Borders.emptyLeft(20),
                    JBUI.Borders.emptyRight(20)
                )
            }
        }
    }
}