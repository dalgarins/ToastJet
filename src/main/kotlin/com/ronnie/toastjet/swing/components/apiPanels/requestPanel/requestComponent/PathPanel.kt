package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.ide.ui.laf.darcula.ui.DarculaEditorTextFieldBorder
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.ui.JBColor
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.*
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.MatteBorder

class PathPanel(private val store: RequestStore) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        alignmentX = Component.LEFT_ALIGNMENT
        border = JBUI.Borders.compound(
            JBUI.Borders.empty(2)
        )
//        background = JBColor.background()
        add(createPathHeader())
        repeat(1) {
            add(createPathRow())
        }
    }

    private fun createPathHeader(): JPanel {
        val rowPanel = JPanel(GridBagLayout()).apply {
            preferredSize = Dimension(Int.MAX_VALUE, 30)
            maximumSize = preferredSize
            border = MatteBorder(0, 0, 1, 0, JBColor.GRAY) // Bottom border for separation
            background = JBColor.PanelBackground
        }
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(2, 0)
            fill = GridBagConstraints.HORIZONTAL
        }

        rowPanel.add(createHeaderLabel("VARIABLE"), gbcLayout(gbc, x = 0, weightX = 0.5))
        rowPanel.add(createHeaderLabel("VALUE"), gbcLayout(gbc, x = 1, weightX = 0.5))

        return rowPanel
    }

    private fun createPathRow(): JPanel {
        val rowPanel = JPanel(GridBagLayout()).apply {
            preferredSize = Dimension(Int.MAX_VALUE, 40)
            maximumSize = preferredSize
//            background = JBColor.background()
        }
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(2)
            fill = GridBagConstraints.HORIZONTAL
        }

        listOf(1, 2).forEach { column ->
            gbc.gridx = column
            gbc.weightx = 0.5
            rowPanel.add(createTextField(), gbc)
        }

        return rowPanel
    }

    private fun createHeaderLabel(text: String): JLabel {
        return JLabel(text, SwingConstants.CENTER).apply {
            font = font.deriveFont(Font.BOLD, 14f)
            border = JBUI.Borders.empty(2)
            foreground = JBColor.DARK_GRAY
        }
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
}