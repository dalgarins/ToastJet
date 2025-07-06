package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.swing.store.StateHolder
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel


class ResponseHeadersPanel(theme: StateHolder<EditorColorsManager>, val headers: Map<String,String>) :
    CustomTableWidget(
        cellParameter = listOf(
            CellParameter("Key", 10, 1.0),
            CellParameter("Value", 10, 1.0)
        ),
        theme = theme
    ) {
    override fun constructTableRow() {
        headers.forEach { key, value ->
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
            border = BorderFactory.createEmptyBorder(0, 8, 0, 8)
            maximumSize = Dimension(300, 400)
        }
    }

    init {
        constructTableRow()
    }
}