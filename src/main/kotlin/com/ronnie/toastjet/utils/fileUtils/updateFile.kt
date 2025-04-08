package com.ronnie.toastjet.utils.fileUtils

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.ronnie.toastjet.swing.store.AppStore

fun updateFile(content: String,state:AppStore) {
    val documentManager = FileDocumentManager.getInstance()
    val document = documentManager.getDocument(state.file)

    documentManager.saveAllDocuments()

    if (document != null) {

        WriteCommandAction.runWriteCommandAction(state.project) {
            document.setText(content)
            documentManager.saveDocument(document)
        }
    } else {
        WriteCommandAction.runWriteCommandAction(state.project) {
            state.file.setBinaryContent(content.toByteArray(Charsets.UTF_8))
            state.file.refresh(true, true) // Force refresh on disk
        }
    }

    state.file.refresh(true, true)
}


