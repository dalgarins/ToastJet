package com.ronnie.toastjet.swing.components.apiPanels.requestPanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBIntSpinner
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import javax.swing.*

class RequestOptionsComponent(private val store: RequestStore) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        minimumSize = Dimension(0, 35)
        preferredSize = Dimension(0, 35)
        maximumSize = Dimension(Int.MAX_VALUE, 35)

        val requestType = ComboBox(arrayOf("GET", "POST", "PUT", "PATCH", "DELETE")).apply {
            selectedItem = "GET"
            maximumSize = Dimension(100, preferredSize.height)
        }

        add(requestType)
        add(Box.createHorizontalStrut(10))
        add(JLabel("Timeout:"))
        add(JBIntSpinner(30, 1, 10000, 1))
        add(Box.createHorizontalStrut(10))
        add(JComboBox(arrayOf("s", "ms", "mins")).apply {
            selectedItem = "s"
            maximumSize = Dimension(100, preferredSize.height)
        })
        add(Box.createHorizontalGlue())

        val protocolComboBox = ComboBox(arrayOf("HTTP/1.1", "HTTP/2", "HTTP/3")).apply {
            maximumSize = Dimension(100, preferredSize.height)
            selectedItem = "HTTP/2"
        }

        add(protocolComboBox)
        add(JButton("Send"))
        add(Box.createHorizontalStrut(10))
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }
}