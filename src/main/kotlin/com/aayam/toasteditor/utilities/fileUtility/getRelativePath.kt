package com.aayam.toasteditor.utilities.fileUtility

import java.nio.file.Paths

fun getRelativePath(dirPath: String, docPath: String): String {
    return try {
        val dir = Paths.get(dirPath).toAbsolutePath().normalize()
        val doc = Paths.get(docPath).toAbsolutePath().normalize()

        // Compute the relative path
        val relativePath = dir.relativize(doc).toString()
        println("The relative path we found was $relativePath")
        relativePath
    } catch (e: IllegalArgumentException) {
        // Handle cases where the paths cannot be relativized
        println("Error computing relative path: ${e.message}")
        ""
    }
}
