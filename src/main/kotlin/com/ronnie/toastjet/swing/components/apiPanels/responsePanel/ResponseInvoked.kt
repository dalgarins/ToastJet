package com.ronnie.toastjet.swing.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JPanel

class ResponseInvoked(store: RequestStore) : JPanel() {
    init {
        layout = BorderLayout()
        background = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val title = JLabel("Response").apply {
            font = Font(this.font.name, Font.PLAIN, 20)
            border = JBUI.Borders.empty(0,10,10,0)
            alignmentX = LEFT_ALIGNMENT
        }

        add(title, BorderLayout.NORTH)
        add(JPanel(BorderLayout()).apply {
            preferredSize = Dimension(600,preferredSize.height)
            add(ResponseStatsPanel(store), BorderLayout.NORTH)
            add(ResponseDataPanel(store), BorderLayout.CENTER)
        })
    }
}




