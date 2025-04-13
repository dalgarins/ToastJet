package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder


class FormDataPanel(private val store: RequestStore) : JPanel(BorderLayout()) {

    private val cellBorder = MatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY)
    private val theme = EditorColorsManager.getInstance()

    companion object {
        private const val ENABLED_COL_WIDTH = 30
        private const val TYPE_COL_WIDTH = 100
        private const val DELETE_COL_WIDTH = 30
    }

    private var enabledCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(JLabel(" ").apply {
            preferredSize = Dimension(ENABLED_COL_WIDTH, 30)
            horizontalAlignment = SwingConstants.CENTER
            font = Font(font.name, Font.PLAIN, 15)
            foreground = JBColor.DARK_GRAY
        })
    }

    private var typeCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(JLabel("Type").apply {
            horizontalAlignment = SwingConstants.CENTER
            preferredSize = Dimension(TYPE_COL_WIDTH, 30)
            font = Font(font.name, Font.PLAIN, 15)
            foreground = JBColor.DARK_GRAY
        })
    }

    private var keyCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(JLabel("Key").apply {
            preferredSize = Dimension(0, 30)
            horizontalAlignment = SwingConstants.CENTER
            font = Font(font.name, Font.PLAIN, 15)
            foreground = JBColor.DARK_GRAY
        })
    }

    private var valueCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(JLabel("Value").apply {
            preferredSize = Dimension(0, 30)
            horizontalAlignment = SwingConstants.CENTER
            font = Font(font.name, Font.PLAIN, 15)
            foreground = JBColor.DARK_GRAY
        })
    }

    private var deleteCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        add(JLabel(" ").apply {
            preferredSize = Dimension(DELETE_COL_WIDTH, 30)
            horizontalAlignment = SwingConstants.CENTER
            font = Font(font.name, Font.PLAIN, 15)
            foreground = JBColor.DARK_GRAY
        })
    }

    private val formDataComponent = JPanel().apply {
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.emptyInsets()
        }
        border = JBUI.Borders.compound(JBUI.Borders.emptyTop(5), LineBorder(JBColor.LIGHT_GRAY))
        background = theme.globalScheme.defaultBackground

        add(enabledCol, gbcLayout(gbc, x = 0, y = 0, weightX = 0.0001))
        add(typeCol, gbcLayout(gbc, x = 1, y = 0, weightX = 0.0001))
        add(keyCol, gbcLayout(gbc, x = 2, y = 0, weightX = 0.5))
        add(valueCol, gbcLayout(gbc, x = 3, y = 0, weightX = 0.5))
        add(deleteCol, gbcLayout(gbc, x = 4, y = 0, weightX = 0.0001))
        for (i in 0..10) {
            addRow()
        }
    }


    init {
        background = EditorColorsManager.getInstance().globalScheme.defaultBackground
        val contentPanel = JPanel(BorderLayout()).apply {
            background = EditorColorsManager.getInstance().globalScheme.defaultBackground
            add(formDataComponent, BorderLayout.NORTH)
        }
        val scrollPane = JBScrollPane(contentPanel).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
            border = BorderFactory.createEmptyBorder()
        }
        add(scrollPane, BorderLayout.CENTER)
    }


    private fun addRow() {
        enabledCol.add(
            centeredCell(
                JCheckBox().apply {
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(20, 20)
                    maximumSize = preferredSize
                    horizontalAlignment = SwingConstants.CENTER
                })
        )

        typeCol.add(
            centeredCell(
                ComboBox(arrayOf("Text", "File")).apply {
                    preferredSize = Dimension(TYPE_COL_WIDTH, 30)
                    maximumSize = Dimension(TYPE_COL_WIDTH, 35)
                    background = theme.globalScheme.defaultBackground
                    border = JBUI.Borders.empty()
                }
            ))

        keyCol.add(
            centeredCell(
                JBTextField().apply {
                    border = JBUI.Borders.empty()
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(0, 30) // Width will expand
                }
            ))

        valueCol.add(
            centeredCell(
                JBTextField().apply {
                    border = JBUI.Borders.empty()
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(0, 30) // Width will expand
                }
            ))

        deleteCol.add(
            centeredCell(
                JLabel("x").apply {
                    font = Font(font.name, font.style, 20)
                    foreground = JBColor.RED
                    horizontalAlignment = SwingConstants.CENTER
                    preferredSize = Dimension(DELETE_COL_WIDTH, 30)
                }
            ))
    }

    private fun centeredCell(component: JComponent): JPanel {
        return JPanel(BorderLayout()).apply {
            background = theme.globalScheme.defaultBackground
            border = cellBorder
            add(component, BorderLayout.CENTER)
            preferredSize = Dimension(component.preferredSize.width, 30)
        }
    }


}
