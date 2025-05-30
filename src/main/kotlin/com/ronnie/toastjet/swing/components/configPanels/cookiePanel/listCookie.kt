package com.ronnie.toastjet.swing.components.configPanels.cookiePanel

import com.aayam.toastjet.editor.swing.cookie.createHeaderRow
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.ConfigStore
import java.awt.Dimension
import java.awt.Font
import javax.swing.*

fun listCookie(store: ConfigStore, container: JComponent) {
    val theme = EditorColorsManager.getInstance().globalScheme
    container.apply {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        border = JBUI.Borders.empty(4)
        background = theme.defaultBackground
        foreground = theme.defaultForeground
    }

    fun renderCookies(filter: String): List<JComponent> {
        store.state.getState().cookie.sortBy { it.domain }

        return if (filter == "All") {
            store.state.getState().cookie.mapIndexed { index, cookieClass ->
                individualCookie(store, cookieClass, {
                    container.removeAll()
                    editCookie(store, cookieClass, container)
                    container.repaint()
                    container.revalidate()
                }, { component ->
                    container.remove(component)
                    store.state.setState {
                        it.cookie.removeAt(index)
                        it
                    }
                    container.repaint()
                    container.revalidate()
                })
            }
        } else {
            store.state.getState().cookie.mapIndexedNotNull { index, item ->
                if (item.domain == filter) {
                    individualCookie(
                        store,
                        item,
                        {
                            container.removeAll()
                            editCookie(store, item, container)
                            container.repaint()
                            container.revalidate()
                        },
                        { component ->
                            container.remove(component)
                            store.state.setState {
                                it.cookie.removeAt(index)
                                it
                            }
                            container.repaint()
                            container.revalidate()
                        }
                    )
                } else null
            }
        }
    }

    var listOfCookies = renderCookies("All")


    val settingPart = JPanel().apply {
        background = theme.defaultBackground
        foreground = theme.defaultForeground
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        add(JPanel().apply {
            preferredSize = Dimension(preferredSize.width, 40)
            maximumSize = Dimension(Int.MAX_VALUE, 40)
            layout = BoxLayout(this, BoxLayout.LINE_AXIS)
            add(JLabel("Domain").apply {
                border = JBUI.Borders.emptyRight(15)
                font = Font(font.name, Font.PLAIN, 18)
            })

            val domainList = store
                .state
                .getState()
                .cookie
                .map { it.domain }.toMutableList()

            domainList.add(0, "All")

            add(
                JComboBox(domainList.toSet().toTypedArray())
                    .apply {
                        addActionListener {
                            listOfCookies.forEach { container.remove(it) }
                            listOfCookies = renderCookies(selectedItem as String)
                            listOfCookies.forEach { container.add(it) }
                            container.repaint()
                            container.revalidate()
                        }
                        preferredSize = Dimension(300, 40)
                        maximumSize = preferredSize
                    })
        })

        add(JButton("\uD83C\uDF6A  Add Cookies").apply {
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {
                        container.removeAll()
                        editCookie(store, null, container)
                    })
            )
        })
    }

    val verticalStrut = Box.createVerticalStrut(10)
    val tableHeader = createHeaderRow()
    container.add(settingPart)
    container.add(verticalStrut)
    container.add(tableHeader)
    listOfCookies.forEach { container.add(it) }
}