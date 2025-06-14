package com.ronnie.toastjet.swing.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel

class ResponseInvoked(store: RequestStore) : JPanel() {

    val panel = JPanel(BorderLayout()).apply {
        preferredSize = Dimension(900, preferredSize.height)
        add(ResponseStatsPanel(store), BorderLayout.NORTH)
        add(ResponseDataPanel(store), BorderLayout.CENTER)
    }

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        panel.background = theme.globalScheme.defaultBackground
        panel.foreground = theme.globalScheme.defaultForeground
    }

    init {
        layout = BorderLayout()
        add(panel, BorderLayout.CENTER)
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }
}




