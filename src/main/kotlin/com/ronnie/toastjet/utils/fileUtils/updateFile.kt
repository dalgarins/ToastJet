package com.ronnie.toastjet.utils.fileUtils

import com.ronnie.toastjet.swing.store.AppStore
import java.io.File
import java.nio.charset.StandardCharsets
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager

fun updateFile(content: String, state: AppStore, path: String?) {
    try {
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
                state.file.refresh(true, true)
            }
        }
        state.file.refresh(true, true)
    } catch (e: Exception) {
        path?.let {
            logErrorToFile(content, it)
        }
    }
}

fun logErrorToFile(content: String, errorLogFilePath: String) {
    println("Are we here $content")
    try {
        val errorFile = File(errorLogFilePath)
        errorFile.writeText(content, StandardCharsets.UTF_8)
    } catch (logErr: Exception) {
        println("Failed to write to error log: ${logErr.message}")
    }
}



