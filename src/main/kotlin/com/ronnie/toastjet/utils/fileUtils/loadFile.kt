package com.ronnie.toastjet.utils.fileUtils

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.vfs.VirtualFile

fun loadFile(onFileLoaded : (file:VirtualFile)->Unit){
    val descriptor = FileChooserDescriptor(true, false, false, false, false, false)
        .withTitle("Select Environment File")
        .withDescription("Select .env or environment properties file")

    FileChooser.chooseFiles(descriptor, null, null).firstOrNull()?.let { file ->
        onFileLoaded(file)
    }
}