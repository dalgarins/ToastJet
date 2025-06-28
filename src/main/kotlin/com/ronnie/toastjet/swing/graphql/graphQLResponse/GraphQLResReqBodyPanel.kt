package com.ronnie.toastjet.swing.graphql.graphQLResponse

import com.intellij.lang.Language
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.editor.TJsonLanguage
import com.ronnie.toastjet.model.data.GraphQLData
import com.ronnie.toastjet.model.data.GraphQLResponseData
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class GraphQLResReqBodyPanel(
    val theme: StateHolder<EditorColorsManager>,
    val appStore: AppStore,
    val response: StateHolder<GraphQLResponseData>
) : JPanel() {

    init {
        background = theme.getState().globalScheme.defaultBackground
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
    }

    private fun getBodyComponent(): JComponent {
        val actualRequest = response.getState().apiRequestData



        return ReadonlyGraphQLEditor(
            theme = theme,
            data = actualRequest.graphQL,
            appStore = appStore
        ).apply {
            background = theme.getState().globalScheme.defaultBackground
            foreground = theme.getState().globalScheme.defaultForeground
            theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
        }
    }

    private fun addHeader(label: JComponent) {
        add(JPanel(BorderLayout()).apply {
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener {
                background = it.globalScheme.defaultBackground
            }
            border = JBUI.Borders.empty(10, 10, 5, 10)
            add(label, BorderLayout.WEST)
        })
    }

    private fun addContent(component: JComponent) {
        add(JPanel(BorderLayout()).apply {
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener {
                background = it.globalScheme.defaultBackground
            }
            add(component, BorderLayout.CENTER)
        })
    }

    override fun addNotify() {
        super.addNotify()

        removeAll()

        val typeLabel = JLabel("Body Type : Graph QL").apply {
            foreground = JBColor.GRAY
        }

        val headerPanel = JPanel(FlowLayout(FlowLayout.LEFT, 5, 0)).apply {
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener {
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

class ReadonlyGraphQLEditor(
    theme: StateHolder<EditorColorsManager>, data: GraphQLData, appStore: AppStore
) : JPanel() {


    private val topScrollPane = JBScrollPane(JPanel().apply {
        background = theme.getState().globalScheme.defaultBackground
        foreground = theme.getState().globalScheme.defaultForeground
        theme.addListener {
            background = it.globalScheme.defaultBackground
            foreground = it.globalScheme.defaultForeground
        }
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
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener {
                background = theme.getState().globalScheme.defaultBackground
            }
            val project = appStore.project

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
        background = theme.getState().globalScheme.defaultBackground
        foreground = theme.getState().globalScheme.defaultForeground
        theme.addListener {
            background = it.globalScheme.defaultBackground
            foreground = it.globalScheme.defaultForeground
        }
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
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener {
                background = theme.getState().globalScheme.defaultBackground
            }
            val project = appStore.project

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
        divider.background = theme.getState().globalScheme.defaultBackground
        theme.addListener {
            divider.background = it.globalScheme.defaultBackground
        }
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