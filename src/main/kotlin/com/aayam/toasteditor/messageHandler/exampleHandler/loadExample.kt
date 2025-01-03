package com.aayam.toasteditor.messageHandler.exampleHandler

import com.aayam.toasteditor.utilities.fileUtility.getAbsolutePath
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

fun loadExample(filePath:String,path:String) : String {
    val absolutePath = getAbsolutePath(
        file = filePath,
        relativePath = path
    )
    val file = File(absolutePath)
    try {
        val text = file.readText()
        return text
    }catch (err:Exception){
        println("Failed to read the given file $err")
        return ""
    }
}