package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.lang.Language
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.LightVirtualFile
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import javax.swing.JPanel

class TestPanel (private val store: RequestStore) : JPanel(BorderLayout()) {
    val editor: Editor
    val document: Document
    val theme = EditorColorsManager.getInstance().globalScheme

    init {
        val project = store.appStore.project

        val virtualFile = LightVirtualFile("temp.js", Language.findLanguageByID("javascript") ?: PlainTextLanguage.INSTANCE, store.testState.getState())

        background = theme.defaultBackground

        document = FileDocumentManager.getInstance().getDocument(virtualFile)
            ?: EditorFactory.getInstance().createDocument(store.testState.getState())

        editor = EditorFactory.getInstance().createEditor(document, project)

        if (editor is EditorEx) {
            val editorEx = editor
            editorEx.highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(project, virtualFile)
        }

        document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                store.testState.setState(document.text)
            }
        })

        PsiDocumentManager.getInstance(project).getPsiFile(document)

        add(editor.component, BorderLayout.CENTER)
    }
}