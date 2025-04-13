package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class NonePanel : JPanel() {

    init {
        layout = GridBagLayout()
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
        val label = JLabel("The request does not have any body")
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            weighty = 1.0
            anchor = GridBagConstraints.CENTER
        }

        add(label, gbc)
    }
}
