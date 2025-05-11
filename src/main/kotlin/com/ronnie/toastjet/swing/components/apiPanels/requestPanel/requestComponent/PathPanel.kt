package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.KeyValue
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.LanguageTableCell
import com.ronnie.toastjet.swing.widgets.TableHeaderRenderer
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel
import kotlin.math.min


class PathPanel(private val store: RequestStore) : JPanel() {
    private val table: JBTable
    private val tableModel = DefaultTableModel(arrayOf("Key", "Value"), 0)
    private val scrollPane: JBScrollPane

    private var oldUrl: String = store.state.getState().url

    init {
        tableModel.apply {
            val varState = store.state.getState().path
            varState.forEach {
                addRow(arrayOf(it.key, it.value))
            }
            addTableModelListener {
                if (it.type == TableModelEvent.UPDATE && it.column >= 0) {
                    val row = it.firstRow
                    val newKey = this.getValueAt(row, 0)?.toString() ?: ""
                    val newValue = this.getValueAt(row, 1)?.toString() ?: ""

                    if (row < store.state.getState().path.size) {
                        store.state.setState { state ->
                            state.path[row] = KeyValue(newKey, newValue)
                            state
                        }
                    } else {
                        store.state.setState { state ->
                            state.path.add(KeyValue(newKey, newValue))
                            state
                        }
                    }
                    val vars = store.state.getState()
                    val variableCollection = vars.path.last()
                    if ((variableCollection.key.isNotEmpty() || variableCollection.value.isNotEmpty()) && this.rowCount == vars.headers.size) {
                        addRow(arrayOf("", ""))
                        updatePreferredSize()
                    }
                }
            }
        }
        layout = BorderLayout()
        table = JBTable(tableModel).apply {
            val theme = EditorColorsManager.getInstance()
            background = theme.globalScheme.defaultBackground
            foreground = theme.globalScheme.defaultForeground
            rowHeight = 30
            gridColor = JBColor.LIGHT_GRAY
            setShowGrid(true)
            border = JBUI.Borders.customLineLeft(JBColor.LIGHT_GRAY)
            tableHeader.apply {
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, JBColor.LIGHT_GRAY), // Outer border
                    BorderFactory.createEmptyBorder(2, 2, 2, 2) // Padding
                )

                defaultRenderer = TableHeaderRenderer(JBColor.LIGHT_GRAY)
            }
        }
        scrollPane = JBScrollPane(table)

        val cell = LanguageTableCell(store.appStore)
        for (i in 0..1) {
            with(table.columnModel.getColumn(i)) {
                preferredWidth = 200
                cellEditor = cell.panelEditor
                cellRenderer = cell.renderer
            }
        }

        store.state.addEffect { request ->
            if (oldUrl != request.url) {
                val regex = "\\{(.*?)}".toRegex()
                val oldPathsVars = request.path.map { it.key }
                val matches =
                    regex.findAll(request.url).map { it.groupValues[1] }.filter { it.trim().isNotEmpty() }.toList()
                request.path = request.path.filter { it.key in matches }.toMutableList()
                matches.forEach {
                    if (it !in oldPathsVars) {
                        request.path.add(KeyValue(key = it, value = ""))
                    }
                }
                while (tableModel.rowCount > 0) tableModel.removeRow(0)

                request.path.forEach { tableModel.addRow(arrayOf(it.key, it.value)) }
                updatePreferredSize()
            }
        }


        scrollPane.let {
            it.border = JBUI.Borders.empty()
            it.preferredSize = Dimension(500, 700)
        }
        add(scrollPane, BorderLayout.NORTH)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                updatePreferredSize()
            }
        })

        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    private fun updatePreferredSize() {
        if (scrollPane.verticalScrollBar.isVisible) {
            this.remove(scrollPane)
            this.add(scrollPane, BorderLayout.CENTER)
        } else {
            this.remove(scrollPane)
            this.add(scrollPane, BorderLayout.NORTH)
        }
        scrollPane.preferredSize =
            Dimension(preferredSize.width, min(table.rowCount * table.rowHeight + 30, this.height - 50))
        revalidate()
        repaint()
    }
}

