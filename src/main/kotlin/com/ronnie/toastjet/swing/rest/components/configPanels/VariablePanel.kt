package com.ronnie.toastjet.swing.rest.components.configPanels

import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.swing.store.ConfigStore
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel

class VariablePanel(private val store: ConfigStore) : JPanel(VerticalLayout(UIUtil.DEFAULT_VGAP)) {

    private val tableModel = DefaultTableModel(arrayOf("", "Key", "Value", ""), 0)
    private val table = JBTable(tableModel)
    private var scrollPane: JBScrollPane? = null
    private val cell = LanguageTableCell(store)

    init {
        border = JBUI.Borders.compound(
            JBUI.Borders.emptyTop(10),
            JBUI.Borders.empty(5)
        )
        background = UIUtil.getPanelBackground()

        setupTable()
        rebuildTable()

        scrollPane = JBScrollPane(table).apply {
            preferredSize = Dimension(500, (table.rowHeight * tableModel.rowCount) + table.tableHeader.preferredSize.height + 2)
        }
        add(scrollPane)

        store.state.addListener {
            rebuildTable()
        }
    }

    private fun setupTable() {
        table.apply {
            rowHeight = 30
            gridColor = JBColor.GRAY
            border = JBUI.Borders.customLineLeft(JBColor.GRAY)

            tableHeader.apply {
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, JBColor.GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                )
                defaultRenderer = TableHeaderRenderer(JBColor.GRAY)
            }

            columnModel.getColumn(0).apply {
                preferredWidth = 40
                maxWidth = 40
                cellRenderer = BooleanTableCellRenderer()
                cellEditor = BooleanTableCellEditor()
            }

            for (i in 1..2) {
                columnModel.getColumn(i).apply {
                    preferredWidth = 200
                    cellEditor = cell.panelEditor
                    cellRenderer = cell.renderer
                }
            }

            val tableButton = TableButton {
                val row = editingRow
                if (row != rowCount - 1) {
                    tableModel.removeRow(row)
                    repaint()
                    revalidate()
                    store.state.setState {
                        it.vars.removeAt(row)
                        it
                    }
                }
            }

            columnModel.getColumn(3).apply {
                preferredWidth = 40
                maxWidth = 40
                cellRenderer = tableButton.renderer
                cellEditor = tableButton.editor
            }
        }

        tableModel.addTableModelListener { event ->
            if (event.type == TableModelEvent.UPDATE && event.column >= 0) {
                val row = event.firstRow
                val newKey = tableModel.getValueAt(row, 1)?.toString() ?: ""
                val newValue = tableModel.getValueAt(row, 2)?.toString() ?: ""
                val newEnabled = tableModel.getValueAt(row, 0) as? Boolean ?: false

                val state = store.state.getState()
                if (row < state.vars.size) {
                    store.state.setState {
                        it.vars[row] = KeyValueChecked(newEnabled, newKey, newValue)
                        it
                    }
                } else {
                    store.state.setState {
                        it.vars.add(KeyValueChecked(newEnabled, newKey, newValue))
                        it
                    }
                }

                val vars = store.state.getState().vars
                val last = vars.lastOrNull()
                if ((last?.key?.isNotEmpty() == true || last?.value?.isNotEmpty() == true) && tableModel.rowCount == vars.size) {
                    tableModel.addRow(arrayOf(true, "", "", ""))
                    scrollPane?.apply {
                        preferredSize = Dimension(preferredSize.width, preferredSize.height + 30)
                        revalidate()
                        repaint()
                    }
                }
            }
        }
    }

    private fun rebuildTable() {
        tableModel.setRowCount(0)
        val vars = store.state.getState().vars
        vars.forEach {
            tableModel.addRow(arrayOf(it.isChecked, it.key, it.value, ""))
        }

        if (vars.isEmpty() || vars.last().key.isNotEmpty() || vars.last().value.isNotEmpty()) {
            tableModel.addRow(arrayOf(true, "", "", ""))
        }

        scrollPane?.preferredSize = Dimension(500, (table.rowHeight * tableModel.rowCount) + table.tableHeader.preferredSize.height + 2)
        scrollPane?.revalidate()
        scrollPane?.repaint()
    }
}
