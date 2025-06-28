package com.ronnie.toastjet.swing.widgets

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.RadioButton
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.StateHolder
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
    private val theme: StateHolder<EditorColorsManager>,
    private val tabs: MutableList<Tabs> = mutableListOf(),
    private val action: MutableList<TabbedAction> = mutableListOf(),
) : JPanel(BorderLayout()) {

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

    override fun removeAll() {
        super.removeAll()
        this.tabs.removeAll { true }
    }

    init {
        background = theme.getState().globalScheme.defaultBackground
        foreground = theme.getState().globalScheme.defaultForeground
        theme.addListener {
            background = theme.getState().globalScheme.defaultBackground
            foreground = theme.getState().globalScheme.defaultForeground
        }
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
            background = theme.getState().globalScheme.defaultBackground
            foreground = theme.getState().globalScheme.defaultForeground
            theme.addListener {
                background = theme.getState().globalScheme.defaultBackground
                foreground = theme.getState().globalScheme.defaultForeground
            }
            add(JPanel(FlowLayout(FlowLayout.LEFT, 5, 0)).apply {
                background = theme.getState().globalScheme.defaultBackground
                foreground = theme.getState().globalScheme.defaultForeground
                theme.addListener {
                    background = theme.getState().globalScheme.defaultBackground
                    foreground = theme.getState().globalScheme.defaultForeground
                }
                border = JBUI.Borders.emptyTop(5)
                tabs.forEachIndexed { index, tab ->
                    add(tab.radioButton.apply {
                        background = theme.getState().globalScheme.defaultBackground
                        foreground = theme.getState().globalScheme.defaultForeground
                        theme.addListener {
                            background = theme.getState().globalScheme.defaultBackground
                            foreground = theme.getState().globalScheme.defaultForeground
                        }
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
                        background = theme.getState().globalScheme.defaultBackground
                        foreground = theme.getState().globalScheme.defaultForeground
                        theme.addListener {
                            background = theme.getState().globalScheme.defaultBackground
                            foreground = theme.getState().globalScheme.defaultForeground
                        }
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
                background = theme.getState().globalScheme.defaultBackground
                foreground = theme.getState().globalScheme.defaultForeground
                theme.addListener {
                    background = theme.getState().globalScheme.defaultBackground
                    foreground = theme.getState().globalScheme.defaultForeground
                }
                action.forEach { action ->
                    add(JButton(action.title).apply {
                        background = theme.getState().globalScheme.defaultBackground
                        foreground = theme.getState().globalScheme.defaultForeground
                        theme.addListener {
                            background = theme.getState().globalScheme.defaultBackground
                            foreground = theme.getState().globalScheme.defaultForeground
                        }
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