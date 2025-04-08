package com.ronnie.toastjet.utils.fileUtils

import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.ronnie.toastjet.model.data.EnvData

fun parseEnvFile(file: VirtualFile): List<EnvData>? {
    return try {
        val content = VfsUtilCore.loadText(file)
        content.lines()
            .map { it.trim() } // Trim each line
            .filterNot { it.startsWith("#") || it.isEmpty() } // Ignore comments and empty lines
            .mapNotNull { line ->
                val parts = line.split("=", limit = 2)
                when {
                    parts.size == 2 -> {
                        val key = parts[0].trim().removeSurrounding("\"")
                        val value = parts[1].trim().removeSurrounding("\"")
                        if (key.isNotEmpty() && value.isNotEmpty()) {
                            EnvData(key = key, value = value, enabled = true, path = file.path)
                        } else {
                            null
                        }
                    }
                    else -> null
                }
            }
    } catch (e: Exception) {
        null
    }
}