package com.ronnie.toastjet.utils.fileUtils


import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import java.io.File
import java.io.IOException


fun saveFileContent(content: String, filePath: String) {
    try {
        WriteAction.run<IOException> {
            val parentPath = File(filePath).parent
            val fileName = File(filePath).name

            val parentDir = LocalFileSystem.getInstance()
                .refreshAndFindFileByPath(parentPath)
                ?: VfsUtil.createDirectoryIfMissing(parentPath)
                ?: throw IOException("Failed to create parent directory")

            val virtualFile = parentDir.findChild(fileName)
                ?: parentDir.createChildData(null, fileName)

            virtualFile.setBinaryContent(content.toByteArray(Charsets.UTF_8))
        }
    } catch (e: Exception) {
        println("Failed to write to file: ${e.message}")
    }
}