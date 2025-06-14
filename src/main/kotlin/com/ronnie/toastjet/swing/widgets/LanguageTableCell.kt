package com.ronnie.toastjet.swing.widgets

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.ui.JBColor
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.Component
import java.awt.Cursor
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import javax.swing.AbstractCellEditor
import javax.swing.JLabel
import javax.swing.JTable
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer


class LanguageTableCell(val state: AppStore,val theme: StateHolder<EditorColorsManager>) {
    private val editorTextField = LanguageTextField(PlainTextLanguage.INSTANCE, state.project, "", true).apply {
        border = JBUI.Borders.compound(JBUI.Borders.empty(5))
        background = theme.getState().globalScheme.defaultBackground
        foreground = theme.getState().globalScheme.defaultForeground

        addSettingsProvider {
            editor?.contentComponent?.background = JBColor.background()
            editor?.component?.background = JBColor.background()
        }

        cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)

        addFocusListener(object : FocusListener {
            override fun focusGained(e: FocusEvent?) {
                background = JBColor.background()
                border = JBUI.Borders.customLine(JBColor.background(),5)
            }

            override fun focusLost(e: FocusEvent?) {
                border = JBUI.Borders.compound(JBUI.Borders.empty(5))
                background = theme.getState().globalScheme.defaultBackground
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
                background = theme.getState().globalScheme.defaultBackground
                foreground = theme.getState().globalScheme.defaultForeground
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
