package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.editor.TJsonLanguage
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JLabel
import javax.swing.JPanel

class GraphQLEditor(val store: RequestStore) : JPanel() {

    private val topScrollPane = JBScrollPane(JPanel().apply {
        background = store.theme.getState().globalScheme.defaultBackground
        store.theme.addListener {
            background = it.globalScheme.defaultBackground
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
            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
            val project = store.appStore.project

            val virtualFile = LightVirtualFile("temp.query", PlainTextLanguage.INSTANCE, store.graphQlState.getState().query)

            val document = FileDocumentManager.getInstance().getDocument(virtualFile)
                ?: EditorFactory.getInstance().createDocument(store.graphQlState.getState().query)

            val editor = EditorFactory.getInstance().createEditor(document, project)

            if (editor is EditorEx) {
                editor.highlighter =
                    EditorHighlighterFactory.getInstance().createEditorHighlighter(project, virtualFile)
            }

            document.addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    store.graphQlState.setState {
                        it.query = document.text
                        it
                    }
                }
            })

            PsiDocumentManager.getInstance(project).getPsiFile(document)

            add(editor.component, BorderLayout.CENTER)
        }, BorderLayout.CENTER)
    }).apply {
        preferredSize = Dimension(600, this.preferredHeight)
    }

    private val bottomScrollPane = JBScrollPane(JPanel().apply {
        background = store.theme.getState().globalScheme.defaultBackground
        store.theme.addListener {
            background = it.globalScheme.defaultBackground
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
            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
            val project = store.appStore.project

            val virtualFile = LightVirtualFile("temp.json", TJsonLanguage.INSTANCE, store.graphQlState.getState().variable)

            val document = FileDocumentManager.getInstance().getDocument(virtualFile)
                ?: EditorFactory.getInstance().createDocument(store.graphQlState.getState().variable)

            val editor = EditorFactory.getInstance().createEditor(document, project)

            if (editor is EditorEx) {
                editor.highlighter =
                    EditorHighlighterFactory.getInstance().createEditorHighlighter(project, virtualFile)
            }

            document.addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    store.graphQlState.setState {
                        it.variable = document.text
                        it
                    }
                }
            })

            PsiDocumentManager.getInstance(project).getPsiFile(document)

            add(editor.component, BorderLayout.CENTER)
        }, BorderLayout.CENTER)
    })


    private val splitter = OnePixelSplitter(false, 0.5f).apply {
        dividerWidth = 1
        divider.background = store.theme.getState().globalScheme.defaultBackground
        store.theme.addListener {
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