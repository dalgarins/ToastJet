package com.ronnie.toastjet.swing.widgets

import com.intellij.ui.JBColor
import com.intellij.ui.components.CheckBox
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import java.awt.Component
import java.awt.Font
import javax.swing.*
import javax.swing.table.TableCellRenderer

class TableButton(private val action: () -> Unit) {

    private val button = JLabel("x").apply {
        font = Font(font.name, font.style, 18)
        foreground = JBColor.RED
        horizontalAlignment = SwingConstants.CENTER
        addMouseListener(SwingMouseListener(mousePressed = { action() }))
    }

    val renderer = ButtonRenderer()
    val editor = ButtonEditor(JCheckBox())

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
            return JLabel("x").apply {
                font = Font(font.name, font.style, 18)
                foreground = JBColor.RED
                horizontalAlignment = SwingConstants.CENTER
                addMouseListener(SwingMouseListener(mousePressed = { this@TableButton.action() }))
            }
        }

        override fun getCellEditorValue(): Any {
            return true
        }
    }
}