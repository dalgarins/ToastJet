package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.KeyValue
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.widgets.CellParameter
import com.ronnie.toastjet.swing.widgets.CustomTableWidget
import java.awt.Dimension
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class PathPanel(private val store: RequestStore) : CustomTableWidget(
    cellParameter = listOf(
        CellParameter(
            title = "Key",
            baseWidth = 40,
            weight = 1.0
        ),
        CellParameter(
            title = "Value",
            baseWidth = 40,
            weight = 1.0
        )
    )
) {
    var oldUrl = store.state.getState().url

    fun getRowComponent(index: Int, p: KeyValue): List<JComponent> {


        val keyPanel = JBTextField().apply {
            text = p.key
            border = JBUI.Borders.empty()
            background = theme.globalScheme.defaultBackground
            preferredSize = Dimension(0, 30)
            isEditable = false

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
                        if (it.path.size > index) {
                            it.path[index].key = this@apply.text
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
                        if (it.path.size > index) {
                            it.path[index].value = this@apply.text
                        }
                        it
                    }
                }
            })
        }

        return listOf(keyPanel, valuePanel)
    }

    override fun constructTableRow() {
        val paths = store.state.getState().path
        paths.forEachIndexed { index, p ->
            addRow(getRowComponent(index, p))
        }
    }

    init {
        constructTableRow()
        store.state.addEffect { request ->
            if (oldUrl != request.url) {
                val regex = "(?<!\\{)\\{([^{}]+)}(?!})".toRegex()
                val oldPathsVars = request.path.map { it.key }
                val matches =
                    regex.findAll(request.url).map { it.groupValues[1] }.filter { it.trim().isNotEmpty() }.toList()
                request.path = request.path.filter { it.key in matches }.toMutableList()
                matches.forEach {
                    if (it !in oldPathsVars) {
                        request.path.add(KeyValue(key = it, value = ""))
                    }
                }
                restore()
                this.oldUrl = store.state.getState().url
            }
        }
    }
}