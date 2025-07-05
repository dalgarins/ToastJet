package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.LightVirtualFile
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.BorderLayout
import javax.swing.JPanel

class TextEditor(data: StateHolder<String>,appStore: AppStore) : JPanel(BorderLayout()) {

    val editor: Editor
    val document: Document

    init {
        val project = appStore.project

        val virtualFile = LightVirtualFile("temp.json", PlainTextFileType.INSTANCE, data.getState())

        document = FileDocumentManager.getInstance().getDocument(virtualFile)
            ?: EditorFactory.getInstance().createDocument(data.getState())

        editor = EditorFactory.getInstance().createEditor(document, project)

        if (editor is EditorEx) {
            val editorEx = editor
            editorEx.highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(project, virtualFile)
        }

        document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                data.setState(document.text)
            }
        })

        PsiDocumentManager.getInstance(project).getPsiFile(document)

        add(editor.component, BorderLayout.CENTER)
    }
}
