package com.ronnie.toastjet.utils.fileUtils

import com.intellij.openapi.vfs.VirtualFile
import java.io.File

fun findConfigFile(startPath: String, targetFile: String = "config.toos"): String? {
    val startFile = File(startPath)
    if (isConfigFile(startPath)) {
        return startPath
    }

    var currentDir = startFile.parentFile
    while (currentDir != null) {
        val potentialFilePath = currentDir.resolve(targetFile)
        if (potentialFilePath.exists()) {
            return potentialFilePath.path
        }

        val parentDir = currentDir.parentFile
        if (parentDir === currentDir) {
            break
        }

        currentDir = parentDir
    }

    return null
}

private fun isConfigFile(path: String): Boolean {
    val fileName = File(path).nameWithoutExtension
    return fileName == "config"
}


fun loadConfiguration(file:VirtualFile){

}