package com.ronnie.toastjet.swing.components.configPanels

import com.intellij.ui.JBColor
import com.intellij.ui.components.CheckBox
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import java.awt.Component
import java.awt.Font
import javax.swing.DefaultCellEditor
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.table.TableCellRenderer

class TableButton(action: () -> Unit) {

    private val button = JLabel("x").apply {
        font = Font(font.name, font.style, 18)
        foreground = JBColor.RED
        horizontalAlignment = SwingConstants.CENTER
        addMouseListener(SwingMouseListener(mousePressed = { action() }))
    }

    val renderer = ButtonRenderer()
    val editor = ButtonEditor(CheckBox(""))

    inner class ButtonRenderer : JButton(), TableCellRenderer {
        init {
            isOpaque = true
        }

        override fun getTableCellRendererComponent(
            table: JTable, value: Any?,
            isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ): Component = button

    }

    inner class ButtonEditor(checkBox: JCheckBox) : DefaultCellEditor(checkBox) {

        override fun getTableCellEditorComponent(
            table: JTable, value: Any?,
            isSelected: Boolean, row: Int, column: Int
        ): Component {
            return button
        }

        override fun getCellEditorValue(): Any {
            return true
        }
    }
}
