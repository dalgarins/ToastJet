package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent


import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextField
import com.intellij.util.queryParameters
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.net.URI
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class ParamsPanel(private val store: RequestStore) : CustomTableWidget(
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

    var oldUrl = store.state.getState().url

    var isActive = false


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
            addFocusListener(object : FocusListener {
                override fun focusGained(p0: FocusEvent?) {
                    isActive = true
                }

                override fun focusLost(p0: FocusEvent?) {
                    isActive = false
                }

            })
            background = theme.globalScheme.defaultBackground
            preferredSize = Dimension(this@ParamsPanel.cellParameter[0].baseWidth, 20)
            maximumSize = preferredSize
            horizontalAlignment = SwingConstants.CENTER
        }

        val keyPanel = JBTextField().apply {
            text = p.key
            border = JBUI.Borders.empty()
            background = theme.globalScheme.defaultBackground
            preferredSize = Dimension(0, 30)

            addFocusListener(object : FocusListener {
                override fun focusGained(p0: FocusEvent?) {
                    isActive = true
                }

                override fun focusLost(p0: FocusEvent?) {
                    isActive = false
                }

            })

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
                        if (it.params.size > index) {
                            it.params[index].key = this@apply.text
                        } else {
                            val kvc = KeyValueChecked(true, "", "")
                            it.params.add(kvc)
                            addRow(getRowComponent(store.state.getState().params.size, kvc))
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

            addFocusListener(object : FocusListener {
                override fun focusGained(p0: FocusEvent?) {
                    isActive = true
                }

                override fun focusLost(p0: FocusEvent?) {
                    isActive = false
                }

            })

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
                        if (it.params.size > index) {
                            it.params[index].value = this@apply.text
                        } else {
                            val kvc = KeyValueChecked(true, "", "")
                            it.params.add(kvc)
                            addRow(getRowComponent(store.state.getState().params.size, kvc))
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
            preferredSize = Dimension(this@ParamsPanel.cellParameter[3].baseWidth, 30)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            addFocusListener(object : FocusListener {
                override fun focusGained(p0: FocusEvent?) {
                    isActive = true
                }

                override fun focusLost(p0: FocusEvent?) {
                    isActive = false
                }

            })

            addMouseListener(
                SwingMouseListener(
                    mousePressed = {
                        store.state.setState {
                            it.params.removeAt(index)
                            it
                        }
                        this@ParamsPanel.restore()
                    }
                )
            )
        }
        return listOf(enablePanel, keyPanel, valuePanel, deleteCol)
    }

    override fun constructTableRow() {
        val params = store.state.getState().params
        params.forEachIndexed { index, p ->
            addRow(getRowComponent(index, p))
        }
        val lastParams = params.lastOrNull()
        if (lastParams == null || lastParams.key.trim().isNotEmpty() || lastParams.value.trim().isNotEmpty()) {
            addRow(getRowComponent(params.size, KeyValueChecked(isChecked = true)))
        }
    }

    init {
        constructTableRow()
        store.state.addEffect { request ->
            println("Testing the equality $oldUrl ${oldUrl != request.url} ${request.url}")
            if (oldUrl != request.url && !isActive) {
                val regex = "\\{(.*?)}".toRegex()
                val oldEnabledParams = request.params.filter { it.isChecked }
                val modifiedUrl = request.url.replace(regex, "")
                val uri = URI(modifiedUrl)
                val uriParams = uri.queryParameters.map {
                    KeyValueChecked(key = it.key, value = it.value, isChecked = true)
                }.toMutableList()
                val newEnabledParams = uriParams.map { it.key }
                oldEnabledParams.forEach {
                    if (it.key !in newEnabledParams) {
                        uriParams.add(KeyValueChecked(key = it.key, value = it.value, isChecked = false))
                    }
                }
                request.params = uriParams
                restore()
                this.oldUrl = request.url
            }
        }
    }

}