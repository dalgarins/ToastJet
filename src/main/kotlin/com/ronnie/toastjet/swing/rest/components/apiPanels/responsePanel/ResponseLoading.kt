package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.*
import javax.swing.*

class ResponseLoading(val theme: StateHolder<EditorColorsManager>) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
    }

    init {
        layout = GridBagLayout()
        setTheme(theme.getState())
        theme.addListener(this::setTheme)
        val constraints = GridBagConstraints().apply {
            gridx = 0
            fill = GridBagConstraints.HORIZONTAL
            anchor = GridBagConstraints.CENTER
            insets = JBUI.insets(10)
        }

        val messageLabel = JLabel("API is being invoked").apply {
            foreground = JBColor.BLUE
            font = Font("Sans", Font.BOLD, 18)
            horizontalAlignment = SwingConstants.CENTER
        }
        constraints.gridy = 0
        add(messageLabel, constraints)

        val progressBar = JProgressBar().apply {
            isIndeterminate = true
        }
        constraints.gridy = 1
        constraints.fill = GridBagConstraints.NONE
        add(progressBar, constraints)

        val cancelButton = JButton("Cancel").apply {

        }
        constraints.gridy = 2
        add(cancelButton, constraints)
    }
}
