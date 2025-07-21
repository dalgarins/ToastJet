package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.*
import javax.swing.*

class ResponseNotInvoked(val theme: StateHolder<EditorColorsManager>) : JPanel() {
    val label = JLabel("API not invoked").apply {
        foreground = JBColor.RED
        font = Font("Sans", Font.PLAIN, 30)
    }
    val wrapper = JPanel(GridBagLayout()).apply {
        add(label)
    }

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        wrapper.background = theme.globalScheme.defaultBackground
    }

    init {
        layout = GridBagLayout()
        add(wrapper, GridBagConstraints())
        background = theme.getState().globalScheme.defaultBackground
        setTheme(theme.getState())
        theme.addListener(this::setTheme)
    }
}