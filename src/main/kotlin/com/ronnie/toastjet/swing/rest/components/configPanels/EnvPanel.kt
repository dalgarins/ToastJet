package com.ronnie.toastjet.swing.rest.components.configPanels

import com.intellij.icons.AllIcons
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.BooleanTableCellEditor
import com.intellij.ui.BooleanTableCellRenderer
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.ui.table.JBTable
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import com.ronnie.toastjet.model.data.EnvData
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.utils.fileUtils.loadFile
import com.ronnie.toastjet.utils.fileUtils.parseEnvFile
import net.miginfocom.swing.MigLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.*
import javax.swing.event.TableModelEvent
import javax.swing.table.DefaultTableModel

class EnvPanel(val store: ConfigStore) : JPanel(VerticalLayout(UIUtil.DEFAULT_VGAP)) {

    init {
        border = JBUI.Borders.empty(10)
        background = store.theme.getState().globalScheme.defaultBackground
        foreground = store.theme.getState().globalScheme.defaultForeground
        store.theme.addListener {
            background = it.globalScheme.defaultBackground
            foreground = it.globalScheme.defaultForeground
        }

        val envTables = JPanel()

        add(createSectionHeader("Environment Variables"))
        add(createMainEnvTable())
        add(createSpacer(10))

        add(createSectionHeader("Loaded Environment Files"))
        add(envTables)
        add(createFileLoaderButton({ file ->
            store.state.setState { state ->
                state.envPath.add(file.path)
                state
            }
            val envs = parseEnvFile(file)
            val component = createEnvFileSection(file.path, envs)
            envTables.add(component)
            add(createSpacer(5))
            add(component)
            repaint()
            revalidate()
        }))
    }

    private fun createSectionHeader(title: String): JComponent {
        return JLabel(title).apply {
            font = UIUtil.getLabelFont().deriveFont(Font.BOLD, 14f)
            foreground = JBColor(0x616161, 0x8C8C8C)
            alignmentX = Component.LEFT_ALIGNMENT
        }
    }

    private fun createMainEnvTable(): JComponent {
        var scrollPane: JBScrollPane? = null
        val tableModel = DefaultTableModel(arrayOf("", "Key", "Value", ""), 0).apply {
            val envState = store.state.getState().envs
            val nonFileEnv = envState.filter { it.path.isEmpty() }
            nonFileEnv.forEach {
                addRow(arrayOf(it.enabled, it.key, it.value, ""))
            }
            if (nonFileEnv.isEmpty()) {
                addRow(arrayOf(true, "", "", ""))
            } else {
                val lastEnv = nonFileEnv.last()
                if ((lastEnv.key.isNotEmpty() || lastEnv.value.isNotEmpty()) && this.rowCount == envState.size) {
                    addRow(arrayOf(true, "", "", ""))
                }
            }
            addTableModelListener {
                if (it.type == TableModelEvent.UPDATE && it.column >= 0) {
                    val row = it.firstRow
                    val newKey = this.getValueAt(row, 1)?.toString() ?: ""
                    val newValue = this.getValueAt(row, 2)?.toString() ?: ""
                    val newEnabled = this.getValueAt(row, 0) as? Boolean ?: false

                    if (row < store.state.getState().envs.size) {
                        store.state.setState { state ->
                            state.envs[row] = EnvData(newKey, newEnabled, newValue, "")
                            state
                        }
                    } else {
                        store.state.setState { state ->
                            state.envs.add(EnvData(newKey, newEnabled, newValue, ""))
                            state
                        }
                    }
                    val env = store.state.getState().envs
                    val envCollection = env.last()
                    if ((envCollection.key.isNotEmpty() || envCollection.value.isNotEmpty()) && this.rowCount == env.size) {
                        addRow(arrayOf(true, "", "", ""))
                        if (scrollPane != null) {
                            scrollPane!!.preferredSize =
                                Dimension(scrollPane!!.preferredSize.width, scrollPane!!.preferredSize.height + 32)
                            scrollPane!!.repaint()
                            scrollPane!!.revalidate()
                        }
                    }
                }
            }
        }

        val table = JBTable(tableModel).apply {
            rowHeight = 32
            gridColor = JBColor.GRAY
            border = JBUI.Borders.customLineLeft(JBColor.GRAY)
            tableHeader.apply {
                background = JBColor.LIGHT_GRAY
                foreground = JBColor.DARK_GRAY
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, JBColor.GRAY), // Outer border
                    BorderFactory.createEmptyBorder(2, 2, 2, 2) // Padding
                )
                defaultRenderer = TableHeaderRenderer(JBColor.GRAY)
            }
            showHorizontalLines = true
            showVerticalLines = true
            columnModel.apply {
                getColumn(0).apply {
                    preferredWidth = 40
                    maxWidth = 40
                    cellRenderer = BooleanTableCellRenderer().withCheckboxCentered()
                    cellEditor = BooleanTableCellEditor()
                }

                (1..2).forEach { col ->
                    getColumn(col).preferredWidth = 220
                }
            }
        }

        with(table.columnModel.getColumn(3)) {
            val tableButton = TableButton {
                val row = table.editingRow
                if (row != table.rowCount - 1) {
                    tableModel.removeRow(row)
                    table.repaint()
                    table.revalidate()
                    store.state.setState {
                        it.envs.removeAt(row)
                        it
                    }
                }
            }
            preferredWidth = 40
            maxWidth = 40
            cellRenderer = tableButton.renderer
            cellEditor = tableButton.editor
        }

        val cell = LanguageTableCell(store)
        (1..2).forEach { col ->
            table.columnModel.getColumn(col).apply {
                cellEditor = cell.panelEditor
                cellRenderer = cell.renderer
            }
        }

        return JBScrollPane(table).apply {
            scrollPane = this
            preferredSize =
                Dimension(500, (table.rowHeight * tableModel.rowCount) + table.tableHeader.preferredSize.height + 2)
        }

    }

    private fun BooleanTableCellRenderer.withCheckboxCentered(): BooleanTableCellRenderer {
        return this.apply {
            horizontalAlignment = SwingConstants.CENTER
        }
    }

    private fun createFileLoaderButton(onFileLoaded: (f: VirtualFile) -> Unit): JComponent {
        return JPanel(MigLayout("ins 0", "[grow]")).apply {
            background = UIUtil.getPanelBackground()
            add(JButton("Load Environment File", AllIcons.General.Settings).apply {
                addActionListener {
                    loadFile(onFileLoaded)
                }
            }, "growx")
        }
    }

    private fun createEnvFileSection(name: String, envs: List<EnvData>?): JComponent {
        return JPanel(VerticalLayout(UIUtil.DEFAULT_HGAP)).apply {
            background = UIUtil.getPanelBackground()

            add(JLabel("File : $name").apply {
                add(createSpacer(5))
                font = UIUtil.getLabelFont().deriveFont(Font.BOLD)
                icon = AllIcons.FileTypes.Text
            })

            add(createEnvFileTable(envs))
            repaint()
            revalidate()
        }
    }

    private fun createEnvFileTable(env: List<EnvData>?): JComponent {
        if (env == null) {
            return JLabel("Invalid environment file format")
        }

        val tableModel = object : DefaultTableModel(arrayOf("Key", "Value"), 0) {
            override fun isCellEditable(row: Int, column: Int) = false
        }.apply {
            env.forEach { (k, v) -> addRow(arrayOf(k, v)) }
        }

        val table = JBTable(tableModel).apply {
            rowHeight = 30
            gridColor = JBColor.GRAY
            border = JBUI.Borders.customLineLeft(JBColor.GRAY)
            tableHeader.apply {
                background = JBColor.LIGHT_GRAY
                foreground = JBColor.DARK_GRAY
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, JBColor.GRAY), // Outer border
                    BorderFactory.createEmptyBorder(2, 2, 2, 2) // Padding
                )

                defaultRenderer = TableHeaderRenderer(JBColor.GRAY)
            }
            tableHeader.font = UIUtil.getLabelFont().deriveFont(Font.BOLD)
        }

        return JBScrollPane(table).apply {
            preferredSize =
                Dimension(450, (table.rowHeight * tableModel.rowCount) + table.tableHeader.preferredSize.height + 2)
        }
    }

    private fun createSpacer(height: Int): Component {
        return Box.createRigidArea(Dimension(0, height))
    }


}