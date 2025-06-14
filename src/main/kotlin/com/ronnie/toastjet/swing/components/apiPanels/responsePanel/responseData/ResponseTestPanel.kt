package com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData


import com.intellij.ui.JBColor
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel

class ResponseTestPanel(val store: RequestStore) : CustomTableWidget(
    cellParameter = listOf(
        CellParameter("Test", 10, 1.0),
        CellParameter("Result", 5, 0.25),
    ),
    theme = store.theme
) {
    override fun constructTableRow() {
        val cookies = store.response.getState().tests
        cookies.forEach { test ->
            addRow(
                listOf(
                    createStyledLabel(if (test.name.length > 80) test.name.substring(0, 67) + "..." else test.name),
                    createStyledLabel(if (test.result) "Success" else "Failure").apply {
                        foreground = if (test.result) JBColor.GREEN else JBColor.RED
                    },
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