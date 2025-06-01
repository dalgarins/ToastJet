package com.ronnie.toastjet.swing.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel

class ResponseInvoked(store: RequestStore) : JPanel() {
    init {
        layout = BorderLayout()
        background = EditorColorsManager.getInstance().globalScheme.defaultBackground


        add(JPanel(BorderLayout()).apply {
            preferredSize = Dimension(600, preferredSize.height)
            add(ResponseStatsPanel(store), BorderLayout.NORTH)
            add(ResponseDataPanel(store), BorderLayout.CENTER)
        }, BorderLayout.CENTER)
    }
}




