package com.ronnie.toastjet.swing.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import java.awt.*
import javax.swing.*

class ResponseNotInvoked : JPanel() {

    val theme = EditorColorsManager.getInstance().globalScheme

    init {
        layout = GridBagLayout()
        val label = JLabel("API not invoked")
        label.foreground = JBColor.RED
        label.font = Font("Sans", Font.PLAIN, 30)
        val wrapper = JPanel(GridBagLayout())
        wrapper.background = theme.defaultBackground
        wrapper.add(label)
        add(wrapper, GridBagConstraints())
        background = theme.defaultBackground
    }
}