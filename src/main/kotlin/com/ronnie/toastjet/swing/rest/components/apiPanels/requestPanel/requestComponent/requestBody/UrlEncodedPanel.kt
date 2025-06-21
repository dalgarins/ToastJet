package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody

import javax.swing.JPanel
import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.LanguageTableCell
import com.ronnie.toastjet.swing.widgets.TableButton
import com.ronnie.toastjet.swing.widgets.TableHeaderRenderer
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel
import kotlin.math.min


class UrlEncodedPanel(private val store: RequestStore) : JPanel() {
    private val table: JBTable
    private val tableModel = DefaultTableModel(arrayOf("", "Key", "Value", ""), 0)
    private val scrollPane: JBScrollPane

    init {
        tableModel.apply {
            val varState = store.urlEncodedState.getState()
            varState.forEach {
                addRow(arrayOf(it.isChecked, it.key, it.value, ""))
            }
            if (varState.isEmpty()) {
                addRow(arrayOf(true, "", "", ""))
            } else {
                val lastVal = varState.last()
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

                    if (row < store.urlEncodedState.getState().size) {
                        store.urlEncodedState.setState { state ->
                            state[row] = KeyValueChecked(newEnabled, newKey, newValue)
                            state
                        }
                    } else {
                        store.urlEncodedState.setState { state ->
                            state.add(KeyValueChecked(newEnabled, newKey, newValue))
                            state
                        }
                    }
                    val variableCollection = store.urlEncodedState.getState().last()
                    if ((variableCollection.key.isNotEmpty() || variableCollection.value.isNotEmpty()) && this.rowCount == store.urlEncodedState.getState().size) {
                        addRow(arrayOf(true, "", "", ""))
                        updatePreferredSize()
                    }
                }
            }

        }
        layout = BorderLayout()
        table = JBTable(tableModel).apply {

            background = store.theme.getState().globalScheme.defaultBackground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
            }
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
            columnModel.apply {
                getColumn(0).apply {
                    preferredWidth = 40
                    maxWidth = 40
                    cellRenderer = BooleanTableCellRenderer()
                    cellEditor = BooleanTableCellEditor()
                }
            }
        }
        scrollPane = JBScrollPane(table)

        val cell = LanguageTableCell(store.appStore, store.theme)
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
                store.urlEncodedState.setState {
                    it.removeAt(row)
                    updatePreferredSize()
                    it
                }
            }
        }

        with(table.columnModel.getColumn(3)) {
            preferredWidth = 40
            maxWidth = 40
            cellEditor = tableButton.editor
            cellRenderer = tableButton.renderer
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

        background = store.theme.getState().globalScheme.defaultBackground
        store.theme.addListener {
            background = it.globalScheme.defaultBackground
        }
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
            Dimension(preferredSize.width, min(table.rowCount * table.rowHeight + 30, this@UrlEncodedPanel.height - 50))
        revalidate()
        repaint()
    }
}

