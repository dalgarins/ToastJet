package com.ronnie.toastjet.swing.widgets

import com.intellij.ui.table.JBTable
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.table.TableModel

class CustomBorderTable(model: TableModel,private val color:Color) : JBTable(model) {

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        // Cast to Graphics2D for better control
        val g2 = g as Graphics2D

        // Set color and stroke (optional)
        g2.color = color
        g2.stroke = BasicStroke(1f)

        // Get bounds
        val width = this.width
        val height = this.height

        // Draw custom borders manually
        // Top
        g2.drawLine(0, 0, width, 0)
        // Left
        g2.drawLine(0, 0, 0, height)
        // Bottom
        g2.drawLine(0, height - 1, width, height - 1)
        // Right
        g2.drawLine(width - 1, 0, width - 1, height)
    }
}
