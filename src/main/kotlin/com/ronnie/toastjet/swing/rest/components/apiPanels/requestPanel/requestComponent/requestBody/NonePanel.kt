package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class NonePanel(theme: StateHolder<EditorColorsManager>) : JPanel() {


    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    init {
        layout = GridBagLayout()

        val label = JLabel("The request does not have any body")
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            weighty = 1.0
            anchor = GridBagConstraints.CENTER
        }
        setTheme(theme.getState())
        theme.addListener(this::setTheme)
        add(label, gbc)
    }
}
