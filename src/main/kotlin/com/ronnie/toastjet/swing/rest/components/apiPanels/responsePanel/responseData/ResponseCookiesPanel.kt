package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.swing.store.StateHolder
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel

class ResponseCookiesPanel(
    theme: StateHolder<EditorColorsManager>,
    val cookies : List<CookieData>
) : CustomTableWidget(
    theme = theme,
    cellParameter = listOf(
        CellParameter("Key", 10, 1.0),
        CellParameter("Value", 10, 1.0),
        CellParameter("SameSite", 10, 0.5),
        CellParameter("HttpOnly", 10, 0.5),
        CellParameter("Secure", 10, 0.5),
        CellParameter("Expiry time", 10, 1.0)
    )
) {
    override fun constructTableRow() {
        cookies.forEach { cookie ->
            addRow(
                listOf(
                    createStyledLabel(if (cookie.key.length > 40) cookie.key.substring(0, 57) + "..." else cookie.key),
                    createStyledLabel(
                        if (cookie.value.length > 40) cookie.value.substring(
                            0,
                            57
                        ) + "..." else cookie.value
                    ),
                    createStyledLabel(cookie.sameSite.toString()),
                    createStyledLabel(cookie.httpOnly.toString()),
                    createStyledLabel(cookie.secure.toString()),
                    createStyledLabel(cookie.expiryTime.toString())
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