package com.ronnie.toastjet.swing.widgets

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.StateHolder
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants
import javax.swing.SwingConstants
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder

data class CellParameter(
    val title: String,
    val baseWidth: Int,
    val weight: Double,
)

abstract class CustomTableWidget(
    val cellParameter: List<CellParameter>,
    val theme: StateHolder<EditorColorsManager>
) : JPanel(BorderLayout()) {

    abstract fun constructTableRow()

    private val cellBorder = MatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY)

    private fun getHeader(title: String): JComponent {
        return JLabel(title).apply {
            preferredSize = Dimension(0, 30)
            horizontalAlignment = SwingConstants.CENTER
            font = Font(font.name, Font.PLAIN, 15)
            foreground = JBColor.DARK_GRAY
        }
    }

    private fun getColumn(title: String, lastField: Boolean): JComponent {
        return JPanel().apply {
            layout = VerticalLayout(0)
            background = theme.getState().globalScheme.defaultBackground
            border = MatteBorder(0, 0, 0, if (lastField) 0 else 1, JBColor.LIGHT_GRAY)
            add(getHeader(title))
        }
    }

    private var tableColumns = cellParameter.mapIndexed { index, parameter ->
        getColumn(parameter.title, index == cellParameter.size - 1)
    }

    private var mainLayout = JPanel().apply {
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.emptyInsets()
        }
        border = JBUI.Borders.compound(JBUI.Borders.emptyTop(5), LineBorder(JBColor.LIGHT_GRAY))
        background = theme.getState().globalScheme.defaultBackground
        theme.addListener {
            background = theme.getState().globalScheme.defaultBackground
        }

        for (i in 0..cellParameter.size - 1) {
            add(tableColumns[i], gbcLayout(gbc, x = i, y = 0, weightX = cellParameter[i].weight))
        }
    }

    fun restore() {
        tableColumns.forEachIndexed { index, col ->
            col.removeAll()
            col.add(getHeader(cellParameter[index].title))
        }
        constructTableRow()
        repaint()
        revalidate()
    }

    private val contentPanel = JPanel(BorderLayout()).apply {
        background = theme.getState().globalScheme.defaultBackground
        theme.addListener { background = theme.getState().globalScheme.defaultBackground }
        add(mainLayout, BorderLayout.NORTH)
    }

    private val scrollPane = JBScrollPane(contentPanel).apply {
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        border = BorderFactory.createEmptyBorder()
    }

    fun addRow(component: List<JComponent>) {
        if (component.size == cellParameter.size) {
            for (i in 0..component.size - 1) {
                tableColumns[i].add(centeredCell(component[i]))
            }
        } else {
            throw IllegalArgumentException("Size of component in addRow not equal to size of cellParameter expected ${cellParameter.size} found ${component.size}")
        }
    }

    init {
        background = theme.getState().globalScheme.defaultBackground
        theme.addListener {
            background = theme.getState().globalScheme.defaultBackground
        }
        add(scrollPane, BorderLayout.CENTER)
    }

    private fun centeredCell(component: JComponent): JPanel {
        return JPanel(BorderLayout()).apply {
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener {
                background = theme.getState().globalScheme.defaultBackground
            }
            border = cellBorder
            add(component, BorderLayout.CENTER)
            preferredSize = Dimension(component.preferredSize.width, 30)
        }
    }

}