package com.ronnie.toastjet.swing.components.configPanels


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

fun VariablePanel(store: ConfigStore): JComponent {

    val panel = JPanel(VerticalLayout(UIUtil.DEFAULT_VGAP)).apply {
        border = JBUI.Borders.compound(
            JBUI.Borders.emptyTop(10),
            JBUI.Borders.empty(5)
        )
        background = UIUtil.getPanelBackground()
    }

    var scrollPane: JBScrollPane? = null

    val tableModel = DefaultTableModel(arrayOf("", "Key", "Value", ""), 0).apply {
        val state = store.state.getState()
        state.vars.forEach {
            addRow(arrayOf(it.isChecked, it.key, it.value, ""))
        }
        if (state.vars.isEmpty()) {
            addRow(arrayOf(true, "", "", ""))
        } else {
            val lastVal = state.vars.last()
            if (lastVal.key.isNotEmpty() || lastVal.value.isNotEmpty()) {
                addRow(arrayOf(true, "", "", ""))
            }
        }
        addTableModelListener {
            if (it.type == TableModelEvent.UPDATE && it.column >= 0) {
                val row = it.firstRow
                val newKey = this.getValueAt(row, 1)?.toString() ?: ""
                val newValue = this.getValueAt(row, 2)?.toString() ?: ""
                val newEnabled = this.getValueAt(row, 0) as? Boolean ?: false

                if (row < state.vars.size) {
                    store.state.setState { state ->
                        state.vars[row] = KeyValueChecked(
                            newEnabled,newKey,newValue
                        )
                        state
                    }
                } else {
                   store.state.setState { state ->
                        state.vars.add(KeyValueChecked(newEnabled,newKey, newValue))
                        state
                    }
                }
                val vars = state.vars
                val variableCollection = vars.last()
                if ((variableCollection.key.isNotEmpty() || variableCollection.value.isNotEmpty()) && this.rowCount == vars.size) {
                    addRow(arrayOf(true, "", "", ""))
                    if(scrollPane != null) {
                        scrollPane!!.preferredSize =
                            Dimension(scrollPane!!.preferredSize.width, scrollPane!!.preferredSize.height + 30)
                        scrollPane!!.repaint()
                        scrollPane!!.revalidate()
                    }
                }
            }
        }
    }



    val table = JBTable(tableModel).apply {
        rowHeight = 30
        gridColor = JBColor.GRAY
        border = JBUI.Borders.customLineLeft(JBColor.GRAY)
        tableHeader.apply {
            border = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, JBColor.GRAY), // Outer border
                BorderFactory.createEmptyBorder(2, 2, 2, 2) // Padding
            )

            defaultRenderer = TableHeaderRenderer(JBColor.GRAY)
        }
        columnModel.apply {
            getColumn(0).apply {
                preferredWidth = 40
                maxWidth = 40
                cellRenderer = BooleanTableCellRenderer()
                cellEditor = BooleanTableCellEditor()
            }
        }
    }

    val cell = LanguageTableCell(store)

    for (i in 1..2) {
        with(table.columnModel.getColumn(i)) {
            preferredWidth = 200
            cellEditor = cell.panelEditor
            cellRenderer = cell.renderer
        }
    }

    val tableButton = TableButton {
        val row = table.editingRow
        if (row != table.rowCount - 1) {
            tableModel.removeRow(row)
            table.repaint()
            table.revalidate()
            store.state.setState {
                it.vars.removeAt(row)
                it
            }
        }
    }

    with(table.columnModel.getColumn(3)) {
        preferredWidth = 40
        maxWidth = 40
        cellRenderer = tableButton.renderer
        cellEditor = tableButton.editor
    }

    panel.add(
        JBScrollPane(table).apply {
            scrollPane = this
            preferredSize =
                Dimension(500, (table.rowHeight * tableModel.rowCount) + table.tableHeader.preferredSize.height + 2)
        })

    return panel
}