package com.ronnie.toastjet.swing.rest.components.configPanels

import com.intellij.lang.Language
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.PlainTextFileType
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.psi.PsiFileFactory
import com.ronnie.toastjet.swing.store.ConfigStore
import java.awt.BorderLayout
import javax.swing.JPanel

class FunctionPanel(store: ConfigStore) : JPanel(BorderLayout()) {
    init {
        val js = Language.findLanguageByID("JavaScript") ?: PlainTextLanguage.INSTANCE
        val editorFactory = EditorFactory.getInstance()
        val psiDoc = PsiFileFactory.getInstance(store.appState.project).createFileFromText(js, "")
        val editor = editorFactory.createEditor(
            psiDoc.fileDocument,
            store.appState.project,
            js.associatedFileType ?: PlainTextFileType.INSTANCE,
            false
        )
        editor.document.addDocumentListener(object : DocumentListener {
            override fun documentChanged(event: DocumentEvent) {
                store.state.setState {
                    it.functions = event.document.text
                    it
                }
            }
        })
        add(editor.component, BorderLayout.CENTER)
    }
}