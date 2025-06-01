package com.ronnie.toastjet.swing.widgets

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.RadioButton
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import java.awt.BorderLayout
import java.awt.Cursor
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton


data class Tabs(val title: String, val component: JComponent, val radioButton: JRadioButton = RadioButton(""))
data class TabbedAction(val title: String, val action: (a: Int) -> Unit)

class RadioTabbedPanel(
    private val tabs: MutableList<Tabs> = mutableListOf(),
    private val action: MutableList<TabbedAction> = mutableListOf()
) : JPanel(BorderLayout()) {
    private val theme = EditorColorsManager.getInstance().globalScheme

    var selectedIndex: Int = -1
    var controlComponent: JComponent? = null

    var centralComponent: JComponent? = null

    fun addTab(title: String, component: JComponent) {
        tabs.add(Tabs(title, component, RadioButton("")))
        remove(controlComponent)
        controlComponent = generateControlPanel()
        add(controlComponent!!, BorderLayout.NORTH)
        if (tabs.isNotEmpty()) {
            centralComponent = this.tabs.first().component
            selectedIndex = 0
            add(centralComponent!!, BorderLayout.CENTER)
        }
        revalidate()
    }

    init {
        background = theme.defaultBackground
        foreground = theme.defaultForeground
        controlComponent = generateControlPanel()
        add(controlComponent!!, BorderLayout.NORTH)
        if (tabs.isNotEmpty()) {
            centralComponent = this.tabs.first().component
            selectedIndex = 0
            add(centralComponent!!, BorderLayout.CENTER)
        }
    }

    fun generateControlPanel(): JPanel {
        return JPanel(BorderLayout()).apply {
            background = theme.defaultBackground
            foreground = theme.defaultForeground
            add(JPanel(FlowLayout(FlowLayout.LEFT, 5, 0)).apply {
                background = theme.defaultBackground
                border = JBUI.Borders.emptyTop(5)
                foreground = theme.defaultForeground
                tabs.forEachIndexed { index, tab ->
                    add(tab.radioButton.apply {
                        background = theme.defaultBackground
                        foreground = theme.defaultForeground
                        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        isSelected = index == selectedIndex
                        addActionListener {
                            tabs.forEachIndexed { tabIndex, tab ->
                                tab.radioButton.isSelected = tabIndex == index
                                tab.radioButton.revalidate()
                            }

                            if (index != selectedIndex) {
                                selectedIndex = index
                                this@RadioTabbedPanel.remove(centralComponent)
                                centralComponent = tab.component
                                this@RadioTabbedPanel.add(centralComponent!!, BorderLayout.CENTER)
                                revalidate()
                            }
                        }
                    })
                    add(JLabel(tab.title)).apply {
                        foreground = theme.defaultForeground
                        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        addMouseListener(
                            SwingMouseListener(
                                mousePressed = {
                                    tabs.forEachIndexed { tabIndex, tab ->
                                        tab.radioButton.isSelected = tabIndex == index
                                        tab.radioButton.revalidate()
                                        if (index == tabIndex) {
                                            selectedIndex = index
                                            this@RadioTabbedPanel.remove(centralComponent)
                                            centralComponent = tab.component
                                            this@RadioTabbedPanel.add(centralComponent!!, BorderLayout.CENTER)
                                            revalidate()
                                        }
                                    }
                                }
                            )
                        )
                    }
                }
            }, BorderLayout.WEST)

            add(JPanel(FlowLayout(FlowLayout.RIGHT, 5, 0)).apply {
                background = theme.defaultBackground
                foreground = theme.defaultForeground
                action.forEach { action ->
                    add(JButton(action.title).apply {
                        foreground = theme.defaultForeground
                        background = theme.defaultBackground
                        cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        addActionListener {
                            action.action(selectedIndex)
                        }
                    })
                }
            }, BorderLayout.EAST)
        }
    }
}