package com.aayam.toasteditor.messageHandler

import com.aayam.toasteditor.constants.interfaces.Configuration
import com.google.gson.Gson
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.vfs.VirtualFile

fun initializeHandler(file: VirtualFile): String {
    val theme = EditorColorsManager.getInstance().isDarkEditor
    val font = EditorFontType.getGlobalPlainFont()
    val fontSize = font.size
    val fontFamily = font.family
    val fontName = font.fontName
    val isConfig = file.nameWithoutExtension == "config"
    val configuration = Configuration(
        fontSize = fontSize,
        isDark = theme,
        isConfig = isConfig,
        fontFamily = fontFamily,
        fontName = fontName
    )
    val gSon = Gson()
    return gSon.toJson(configuration)
}