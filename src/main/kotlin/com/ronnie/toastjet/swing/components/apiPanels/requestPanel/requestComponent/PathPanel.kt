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
                    store.pathState.setState {
                        if (it.size > index) {
                            it[index].key = this@apply.text
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
                    store.pathState.setState {
                        if (it.size > index) {
                            it[index].value = this@apply.text
                        }
                        it
                    }
                }
            })
        }

        return listOf(keyPanel, valuePanel)
    }

    override fun constructTableRow() {
        val paths = store.pathState.getState()
        paths.forEachIndexed { index, p ->
            addRow(getRowComponent(index, p))
        }
    }

    init {
        constructTableRow()
        store.urlState.addEffect { newUrl ->
            val regex = "(?<!\\{)\\{([^{}]+)}(?!})".toRegex()
            val oldPathsVars = store.pathState.getState().map { it.key }
            val matches =
                regex.findAll(newUrl).map { it.groupValues[1] }.filter { it.trim().isNotEmpty() }.toList()

            val newPath = store.pathState.getState().filter { it.key in matches }.toMutableList()
            matches.forEach {
                if (it !in oldPathsVars) {
                    newPath.add(KeyValue(key = it, value = ""))
                }
            }
            store.pathState.setState { newPath }
            restore()
        }
    }
}