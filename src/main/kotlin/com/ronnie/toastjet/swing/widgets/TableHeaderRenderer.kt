package com.ronnie.toastjet.swing.widgets

import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Component
import java.awt.Font
import javax.swing.JTable
import javax.swing.border.MatteBorder
import javax.swing.table.DefaultTableCellRenderer


class TableHeaderRenderer(private val color: Color? = null) : DefaultTableCellRenderer() {
    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column).apply {
        if(color != null) {
            border = JBUI.Borders.compound(
                MatteBorder(0, 0, 0, 1, color),
                JBUI.Borders.empty(5)
            )
        }
        horizontalAlignment = CENTER
        font = Font(font.name, Font.PLAIN, 16)
    }
}