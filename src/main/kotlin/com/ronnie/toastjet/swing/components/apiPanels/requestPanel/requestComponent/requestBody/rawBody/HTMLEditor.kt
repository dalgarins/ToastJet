package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody

import com.intellij.ide.highlighter.HtmlFileType
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.LightVirtualFile
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import javax.swing.JPanel

class HTMLEditor(store: RequestStore) : JPanel(BorderLayout()) {

    val editor: Editor
    val document: Document

    init {
        val project = store.appStore.project
        val text = ""

        val virtualFile = LightVirtualFile("temp.json", HtmlFileType.INSTANCE, text)

        document = FileDocumentManager.getInstance().getDocument(virtualFile)
            ?: EditorFactory.getInstance().createDocument(text)

        editor = EditorFactory.getInstance().createEditor(document, project)

        if (editor is EditorEx) {
            val editorEx = editor
            editorEx.highlighter = EditorHighlighterFactory.getInstance().createEditorHighlighter(project, virtualFile)
        }

        PsiDocumentManager.getInstance(project).getPsiFile(document)

        add(editor.component, BorderLayout.CENTER)
    }
}
