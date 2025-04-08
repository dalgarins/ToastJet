package com.aayam.toastjet.utils.uiUtils

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorColors
import java.awt.Color
import javax.swing.UIManager

private var colorMap : Map<String,Color?>? = null

fun getJetBrainsThemeColors(): Map<String, Color?> {

    if(colorMap != null) return colorMap!!
    else {

        val scheme = EditorColorsManager.getInstance().globalScheme

        colorMap = mapOf(
            "Background" to scheme.defaultBackground,
            "Foreground" to scheme.defaultForeground,
            "Caret Color" to scheme.getColor(EditorColors.CARET_COLOR),
            "Selection Background" to scheme.getColor(EditorColors.SELECTION_BACKGROUND_COLOR),
            "Selection Foreground" to scheme.getColor(EditorColors.SELECTION_FOREGROUND_COLOR),
            "Line Number Color" to scheme.getColor(EditorColors.LINE_NUMBERS_COLOR),
            "Gutter Background" to scheme.getColor(EditorColors.GUTTER_BACKGROUND),
            "Link Color" to UIManager.getColor("link.foreground"),
            "Tooltip Background" to UIManager.getColor("ToolTip.background"),
            "Tooltip Foreground" to UIManager.getColor("ToolTip.foreground"),
            "Panel Background" to UIManager.getColor("Panel.background"),
            "Text Field Background" to UIManager.getColor("TextField.background"),
            "Text Field Foreground" to UIManager.getColor("TextField.foreground"),
            "Button Background" to UIManager.getColor("Button.background"),
            "Button Foreground" to UIManager.getColor("Button.foreground")
        )
        return colorMap!!
    }
}
