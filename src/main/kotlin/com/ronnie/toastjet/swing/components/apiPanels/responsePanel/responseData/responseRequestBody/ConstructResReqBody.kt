package com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData.responseRequestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.ui.JBFont
import com.ronnie.toastjet.swing.store.StateHolder
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel

class ConstructResReqBody(val data: Map<String, String>,theme: StateHolder<EditorColorsManager>) : CustomTableWidget(
    cellParameter = listOf(
        CellParameter("Key", 10, 1.0),
        CellParameter("Value", 10, 1.0)
    ),
    theme = theme
) {

    override fun constructTableRow() {
        data.forEach { key, value ->
            addRow(
                listOf(
                    createStyledLabel(key),
                    createStyledLabel(value)
                )
            )
        }
    }

    private fun createStyledLabel(text: String): JLabel {
        return JLabel().apply {
            this.text = if (text.length > 80) text.substring(0, 80) + "..." else text
            font = JBFont.label()
            foreground = theme.getState().globalScheme.defaultForeground
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener {
                foreground = it.globalScheme.defaultForeground
                background = it.globalScheme.defaultBackground
            }
            isOpaque = false
            border = BorderFactory.createEmptyBorder(0, 8, 0, 8)
            maximumSize = Dimension(300, 400)
        }
    }

    init {
        background = theme.getState().globalScheme.defaultBackground
        foreground = theme.getState().globalScheme.defaultForeground
        constructTableRow()
    }
}