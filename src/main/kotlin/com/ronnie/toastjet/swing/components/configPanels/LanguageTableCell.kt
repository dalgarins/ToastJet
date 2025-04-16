package com.ronnie.toastjet.swing.components.configPanels

import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.ui.JBColor
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.ConfigStore
import java.awt.Component
import java.awt.Cursor
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.AbstractCellEditor
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer


class LanguageTableCell(store:ConfigStore) {
    private val editorTextField = LanguageTextField(PlainTextLanguage.INSTANCE, store.appState.project, "", true).apply {
        border = JBUI.Borders.compound(JBUI.Borders.empty(5))
        background = JBColor.background()
        foreground = JBColor.WHITE

        addSettingsProvider {
            editor?.contentComponent?.background = JBColor.background()
            editor?.component?.background = JBColor.background()
        }

        cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)

        addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent?) {
                border = JBUI.Borders.compound(
                    JBUI.Borders.customLine(UIManager.getColor("Tree.selectionBorderColor"),2),
                    JBUI.Borders.empty(5)
                )
            }

            override fun focusLost(e: FocusEvent?) {
                border = JBUI.Borders.empty(5)
            }
        })
    }

    inner class PanelRenderer : TableCellRenderer {
        override fun getTableCellRendererComponent(
            table: JTable, v: Any?, isSelected: Boolean,
            hasFocus: Boolean, row: Int, column: Int
        ): Component {
            return JLabel(v.toString()).apply {
                border = JBUI.Borders.empty(5)
                background = table.background
                foreground = table.foreground
                isOpaque = true
            }
        }
    }

    inner class PanelEditor : TableCellEditor, AbstractCellEditor() {
        override fun getCellEditorValue(): Any {
            return editorTextField.document.text
        }

        override fun getTableCellEditorComponent(
            table: JTable?, value: Any?, isSelected: Boolean, row: Int, column: Int
        ): Component {
            editorTextField.text = value.toString()
            return editorTextField
        }

        override fun stopCellEditing(): Boolean {
            return super.stopCellEditing()
        }


    }

    val renderer = PanelRenderer()
    val panelEditor = PanelEditor()
}
