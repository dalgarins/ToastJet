package com.aayam.toasteditor.messageHandler.requestHandler

import com.aayam.toasteditor.cache.RequestCache
import com.aayam.toasteditor.constants.enums.documentSeparator
import com.aayam.toasteditor.constants.interfaces.message.SaveRequestApiData
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.vfs.VirtualFile

fun saveRequestHandler(
    apiInfo: SaveRequestApiData,
    file: VirtualFile
) {
    if (file.nameWithoutExtension != "config") {
        RequestCache.apis[apiInfo.index] = apiInfo.api.rawCode
        try {
            if (file.isWritable) {
                WriteCommandAction.runWriteCommandAction(null) {
                    val data = RequestCache.apis.joinToString(
                        separator = "\n$documentSeparator\n"
                    )
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
}