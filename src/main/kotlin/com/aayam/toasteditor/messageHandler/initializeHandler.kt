package com.aayam.toasteditor.messageHandler

import com.aayam.toasteditor.constants.interfaces.Configuration
import com.google.gson.Gson
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.vfs.VirtualFile

fun initializeHandler(file: VirtualFile): String {
    val theme = EditorColorsManager.getInstance().isDarkEditor
    val fontSize = EditorFontType.getGlobalPlainFont().size
    val isConfig = file.nameWithoutExtension == "config"
    println("What is the name of the file ${file.nameWithoutExtension} $isConfig")
    val configuration = Configuration(
        fontSize=fontSize,
        theme = theme,
        isConfig = isConfig
    )
    val gSon = Gson()
    return gSon.toJson(configuration)
}