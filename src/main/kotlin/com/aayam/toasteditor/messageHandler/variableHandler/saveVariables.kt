package com.aayam.toasteditor.messageHandler.variableHandler

import com.aayam.toasteditor.cache.VariableCache
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile

fun saveVariablesHandler(file: VirtualFile, data: String) {
    try {
        VariableCache.initialize(data)
        println("The problem is $data")

        if (file.isWritable) {
            WriteCommandAction.runWriteCommandAction(null) {
                file.getOutputStream(null).use { outputStream ->
                    outputStream.write(data.toByteArray(Charsets.UTF_8))
                }
            }
        } else {
            println("File is not writable: ${file.path}")
        }
    } catch (err: Exception) {
        println("Error while writing to the file")
        println(err.message)
    }
}