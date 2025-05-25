package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class HeaderPanel(private val store: RequestStore) : CustomTableWidget(
    cellParameter = listOf(
        CellParameter(
            title = " ",
            baseWidth = 40,
            weight = 0.00005
        ),
        CellParameter(
            title = "Key",
            baseWidth = 40,
            weight = 1.0
        ),
        CellParameter(
            title = "Value",
            baseWidth = 40,
            weight = 1.0
        ),
        CellParameter(
            title = " ",
            baseWidth = 40,
            weight = 0.00005
        )
    )
) {

    fun getRowComponent(index: Int, p: KeyValueChecked): List<JComponent> {

        val enablePanel = JCheckBox().apply {
            isSelected = p.isChecked
            addChangeListener {
                store.state.setState {
                    if (it.params.size > index) {
                        it.params[index].isChecked = isSelected
                    }
                    it
                }
            }
            background = theme.globalScheme.defaultBackground
            preferredSize = Dimension(this@HeaderPanel.cellParameter[0].baseWidth, 20)
            maximumSize = preferredSize
            horizontalAlignment = SwingConstants.CENTER
        }

        val keyPanel = JBTextField().apply {
            text = p.key
            border = JBUI.Borders.empty()
            background = theme.globalScheme.defaultBackground
            preferredSize = Dimension(0, 30)

            document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) {
                    updateFormData()
                }

                override fun removeUpdate(e: DocumentEvent) {
                    updateFormData()
                }

                override fun changedUpdate(e: DocumentEvent) {
                    updateFormData()
                }

                private fun updateFormData() {
                    store.state.setState {
                        if (it.headers.size > index) {
                            it.headers[index].key = this@apply.text
                        } else {
                            val kvc = KeyValueChecked(true, "", "")
                            it.headers.add(kvc)
                            addRow(getRowComponent(store.state.getState().headers.size, kvc))
                        }
                        it
                    }
                }
            })
        }

        val valuePanel = JBTextField().apply {
            text = p.value
            border = JBUI.Borders.empty()
            background = theme.globalScheme.defaultBackground
            preferredSize = Dimension(0, 30)

            document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent) {
                    updateFormData()
                }

                override fun removeUpdate(e: DocumentEvent) {
                    updateFormData()
                }

                override fun changedUpdate(e: DocumentEvent) {
                    updateFormData()
                }

                private fun updateFormData() {
                    store.state.setState {
                        if (it.headers.size > index) {
                            it.headers[index].value = this@apply.text
                        } else {
                            val kvc = KeyValueChecked(true, "", "")
                            it.headers.add(kvc)
                            addRow(getRowComponent(store.state.getState().headers.size, kvc))
                        }
                        it
                    }
                }
            })
        }

        val deleteCol = JLabel("x").apply {
            font = Font(font.name, font.style, 20)
            foreground = JBColor.RED
            horizontalAlignment = SwingConstants.CENTER
            preferredSize = Dimension(this@HeaderPanel.cellParameter[3].baseWidth, 30)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {
                        store.state.setState {
                            it.params.removeAt(index)
                            it
                        }
                        this@HeaderPanel.restore()
                    }
                )
            )
        }
        return listOf(enablePanel, keyPanel, valuePanel, deleteCol)
    }

    override fun constructTableRow() {
        val headers = store.state.getState().headers
        headers.forEachIndexed { index, p ->
            addRow(getRowComponent(index, p))
        }
        val lastParams = headers.lastOrNull()
        if (lastParams == null || lastParams.key.trim().isNotEmpty() || lastParams.value.trim().isNotEmpty()) {
            addRow(getRowComponent(headers.size, KeyValueChecked(isChecked = true)))
        }
    }

    init {
        constructTableRow()
    }

}