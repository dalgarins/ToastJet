package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.JPanel

class TestPanel (private val store: RequestStore) : JPanel() {
    init {
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }
}