package com.ronnie.toastjet.swing.components.apiPanels

import com.intellij.openapi.editor.colors.EditorColorsManager
import javax.swing.JPanel
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import javax.swing.*

class ResponseComponent(private val store: RequestStore) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        minimumSize = Dimension(preferredSize.width, 500)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        isOpaque = true

    }
}

