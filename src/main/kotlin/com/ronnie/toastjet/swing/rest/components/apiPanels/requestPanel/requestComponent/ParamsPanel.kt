package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent


import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextField
import com.intellij.util.queryParameters
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.StateHolder
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


class ParamsPanel(
    private val paramsState: StateHolder<MutableList<KeyValueChecked>>,
    urlState: StateHolder<String>,
    theme: StateHolder<EditorColorsManager>
) : CustomTableWidget(
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
    ), theme
) {


    var isActive = false


    fun getRowComponent(index: Int, p: KeyValueChecked): List<JComponent> {

        val enablePanel = JCheckBox().apply {
            isSelected = p.isChecked
            addChangeListener {
                paramsState.setState {
                    if (it.size > index) {
                        it[index].isChecked = isSelected
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
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener { background = it.globalScheme.defaultBackground }
            preferredSize = Dimension(this@ParamsPanel.cellParameter[0].baseWidth, 20)
            maximumSize = preferredSize
            horizontalAlignment = SwingConstants.CENTER
        }

        val keyPanel = JBTextField().apply {
            text = p.key
            border = JBUI.Borders.empty()
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener { background = it.globalScheme.defaultBackground }
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
                    paramsState.setState {
                        if (it.size > index) {
                            it[index].key = this@apply.text
                        } else {
                            val kvc = KeyValueChecked(true, "", "")
                            it.add(kvc)
                            addRow(getRowComponent(it.size, kvc))
                        }
                        it
                    }
                }
            })
        }

        val valuePanel = JBTextField().apply {
            text = p.value
            border = JBUI.Borders.empty()
            background = theme.getState().globalScheme.defaultBackground
            theme.addListener { background = it.globalScheme.defaultBackground }
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
                    paramsState.setState {
                        if (it.size > index) {
                            it[index].value = this@apply.text
                        } else {
                            val kvc = KeyValueChecked(true, "", "")
                            it.add(kvc)
                            addRow(getRowComponent(it.size, kvc))
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
                        paramsState.setState {
                            it.removeAt(index)
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
        val params = paramsState.getState()
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
        urlState.addEffect { newUrl ->
            if (!isActive) {
                val regex = "\\{(.*?)}".toRegex()
                val oldEnabledParams = paramsState.getState().filter { it.isChecked }
                val modifiedUrl = newUrl.replace(regex, "")
                try {
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
                    paramsState.setState(uriParams)
                    restore()
                } catch (_: Exception) {
                }
            }
        }
    }

}