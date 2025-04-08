package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.icons.AllIcons
import com.intellij.ide.ui.laf.darcula.ui.DarculaEditorTextFieldBorder
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.ui.JBColor
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.*
import javax.swing.*
import javax.swing.border.MatteBorder

class ParamsPanel(private val store: RequestStore) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT
        border = JBUI.Borders.compound(
            JBUI.Borders.empty(2)
        )
//        background = JBColor.background()
        add(createHeader())
        repeat(2) {
            createRow(KeyValueChecked(false, "", ""))
        }
    }


    private fun createHeader(): JPanel {
        val rowPanel = JPanel(GridBagLayout()).apply {
            preferredSize = Dimension(Int.MAX_VALUE, 30)
            maximumSize = preferredSize
            border = MatteBorder(0, 0, 1, 0, JBColor.GRAY)
            background = JBColor.PanelBackground
        }
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(2, 0)
            fill = GridBagConstraints.HORIZONTAL
        }

        rowPanel.add(createHeaderLabel("✔"), gbcLayout(gbc, x = 0, weightX = 0.05))
        rowPanel.add(createHeaderLabel("KEY"), gbcLayout(gbc, x = 1, weightX = 0.45))
        rowPanel.add(createHeaderLabel("VALUE"), gbcLayout(gbc, x = 2, weightX = 0.45))
        rowPanel.add(createHeaderLabel("X").apply {
            foreground = JBColor.RED
        }, gbcLayout(gbc, x = 3, weightX = 0.05))

        return rowPanel
    }

    private fun createHeaderLabel(text: String): JLabel {
        return JLabel(text, SwingConstants.CENTER).apply {
            font = font.deriveFont(Font.BOLD, 14f)
            border = JBUI.Borders.empty(2)
            foreground = JBColor.DARK_GRAY
        }
    }

    private fun createRow(rowData: KeyValueChecked): JPanel {
        val rowPanel = JPanel(GridBagLayout()).apply {
            preferredSize = Dimension(Int.MAX_VALUE, 40)
            maximumSize = preferredSize
//            background = JBColor.background()
        }
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(2)
            fill = GridBagConstraints.HORIZONTAL
        }

        rowPanel.add(JCheckBox().apply {
            isSelected = rowData.isChecked
//            background = JBColor.background()
            addActionListener { rowData.isChecked = isSelected }
        }, gbcLayout(gbc, x = 0, weightX = 0.05))

        listOf(1, 2).forEach { column ->
            rowPanel.add(createTextField(), gbcLayout(gbc, x = column, weightX = 0.45))
        }
        rowPanel.add(createDeleteButton(rowPanel), gbcLayout(gbc, x = 3, weightX = 0.05))
        return rowPanel
    }

    private fun createTextField(): Component {
        val textField = LanguageTextField(PlainTextLanguage.INSTANCE, store.appStore.project, "").apply {
            preferredSize = Dimension(Int.MAX_VALUE, 40)
            addSettingsProvider { editor ->
                editor.setBorder(
                    JBUI.Borders.compound(
                        DarculaEditorTextFieldBorder(this, editor), JBUI.Borders.empty(5)
                    )
                )
            }
        }
        return textField
    }

    private fun createDeleteButton(rowPanel: JPanel): JComponent {
        return JLabel(AllIcons.Actions.DeleteTag).apply {
            foreground = JBColor.RED
            border = JBUI.Borders.empty(3, 5)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {
                        (rowPanel.parent as? JPanel)?.remove(rowPanel)
                        rowPanel.parent?.revalidate()
                        rowPanel.parent?.repaint()
                    })
            )
        }
    }

}