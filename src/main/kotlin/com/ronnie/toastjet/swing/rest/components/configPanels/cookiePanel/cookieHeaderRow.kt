package com.aayam.toastjet.editor.swing.cookie

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

fun createHeaderRow(): JPanel {
    return JPanel().apply {
        preferredSize = Dimension(1000, 30)
        minimumSize = preferredSize
        maximumSize = Dimension(Int.MAX_VALUE, 40)
        background = JBColor.background()
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(2, 0)
            fill = GridBagConstraints.HORIZONTAL
        }

        fun addHeader(title: String, weight: Double, x: Int, width: Int) {
            gbc.gridx = x
            gbc.weightx = weight
            add(JLabel(title, JLabel.CENTER).apply {
                font = font.deriveFont(Font.BOLD)
                preferredSize = Dimension(width, 30)
            }, gbc)
        }

        addHeader("✔", 0.0, 0, 80)
        addHeader("Key", 1.0, 1, 20)
        addHeader("Value", 1.0, 2, 20)
        addHeader("HttpOnly", 0.0, 3, 80)
        addHeader("Secure", 0.0, 4, 80)
        addHeader("Host Only", 0.0, 5, 80)
        addHeader("SameSite", 0.0, 6, 120)
        addHeader("Actions",0.0,7,100)
    }
}