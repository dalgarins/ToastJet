package com.aayam.toasteditor.messageHandler.exampleHandler

import com.intellij.openapi.project.Project
import com.aayam.toasteditor.messageHandler.fileHandler.fileSaverHandler
import com.aayam.toasteditor.utilities.fileUtility.getRelativePath
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

fun saveExample(project:Project,reference:VirtualFile,data:String) :String? {
    val document = fileSaverHandler(project,reference.path)
    try {
        if (document != null) {
            File(document).writeText(data)
            return getRelativePath(reference.path,document)
        }
    }catch(err:Exception){
        println("Failed to write data to the example file $err")
    }
    return null
}