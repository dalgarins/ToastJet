package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData

import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel


class ResponseHeadersPanel(val store: RequestStore) : CustomTableWidget(
    cellParameter = listOf(
        CellParameter("Key", 10, 1.0),
        CellParameter("Value",10, 1.0)
    ),
    theme = store.theme
) {
    override fun constructTableRow() {
        val headers = store.response.getState().responseHeaders
        headers.forEach { key, value ->
            addRow(listOf(
                createStyledLabel(key),
                createStyledLabel(value)
            ))
        }
    }

    private fun createStyledLabel(text: String): JLabel {
        return JLabel().apply {
            this.text = if(text.length > 80) text.substring(0, 80) + "..." else text
            border = BorderFactory.createEmptyBorder(0, 8, 0, 8)
            maximumSize = Dimension(300, 400)
        }
    }

    init {
        constructTableRow()
    }
}