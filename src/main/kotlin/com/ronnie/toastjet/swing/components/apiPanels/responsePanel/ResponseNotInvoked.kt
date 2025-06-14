package com.ronnie.toastjet.swing.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.*
import javax.swing.*

class ResponseNotInvoked(val theme: StateHolder<EditorColorsManager>) : JPanel() {


    init {
        layout = GridBagLayout()
        val label = JLabel("API not invoked")
        label.foreground = JBColor.RED
        label.font = Font("Sans", Font.PLAIN, 30)
        val wrapper = JPanel(GridBagLayout())
        wrapper.background = theme.getState().globalScheme.defaultBackground
        wrapper.add(label)
        add(wrapper, GridBagConstraints())
        background = theme.getState().globalScheme.defaultBackground
        theme.addListener {
            wrapper.background = it.globalScheme.defaultBackground
            background = it.globalScheme.defaultBackground
        }
    }
}