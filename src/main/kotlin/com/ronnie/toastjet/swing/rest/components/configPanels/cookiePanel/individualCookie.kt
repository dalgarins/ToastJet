package com.ronnie.toastjet.swing.rest.components.configPanels.cookiePanel

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.LanguageTextField
import com.intellij.ui.components.CheckBox
import com.intellij.util.IconUtil
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.enums.CookieSameSite
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.ConfigStore
import java.awt.Cursor
import java.awt.Dimension
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

fun individualCookie(
    store: ConfigStore,
    cookie: CookieData,
    onEdit: (a: CookieData) -> Unit,
    onDelete: (m: JComponent) -> Unit
): JPanel {
    return JPanel().apply {
        background = store.theme.getState().globalScheme.defaultBackground
        foreground = store.theme.getState().globalScheme.defaultForeground
        store.theme.addListener {
            background = it.globalScheme.defaultBackground
            foreground = it.globalScheme.defaultForeground
        }
        val parent = this
        preferredSize = Dimension(1000, 40)
        minimumSize = preferredSize
        maximumSize = Dimension(Int.MAX_VALUE, 40)
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(2, 0)
            fill = GridBagConstraints.HORIZONTAL
        }

        fun addComponent(component: JComponent, weight: Double, x: Int, width: Int) {
            gbc.gridx = x
            gbc.weightx = weight
            component.preferredSize = Dimension(width, 30)
            add(component, gbc)
        }

        addComponent(JPanel().apply {
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            add(JCheckBox("").apply {
                isSelected = cookie.enabled
                addActionListener {
                    store.state.setState {
                        cookie.enabled = isSelected
                        it
                    }
                }
            })
        }, 0.0, 0, 80)

        addComponent(LanguageTextField(PlainTextLanguage.INSTANCE, store.appState.project, cookie.key).apply {
            addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    store.state.setState {
                        cookie.key = event.document.text
                        it
                    }
                }
            })
        }, 1.0, 1, 20)
        addComponent(LanguageTextField(PlainTextLanguage.INSTANCE, store.appState.project, cookie.value).apply {
            addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    store.state.setState {
                        cookie.value = event.document.text
                        it
                    }
                }
            })
        }, 1.0, 2, 20)
        addComponent(JPanel().apply {
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            add(CheckBox("").apply {
                isSelected = cookie.httpOnly
                background = store.theme.getState().globalScheme.defaultBackground
                foreground = store.theme.getState().globalScheme.defaultForeground
                store.theme.addListener {
                    background = it.globalScheme.defaultBackground
                    foreground = it.globalScheme.defaultForeground
                }
                addActionListener {
                    store.state.setState {
                        cookie.httpOnly = isSelected
                        it
                    }
                }
            })
        }, 0.0, 3, 80)
        addComponent(JPanel().apply {
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            add(CheckBox("").apply {
                isSelected = cookie.secure
                background = store.theme.getState().globalScheme.defaultBackground
                foreground = store.theme.getState().globalScheme.defaultForeground
                store.theme.addListener {
                    background = it.globalScheme.defaultBackground
                    foreground = it.globalScheme.defaultForeground
                }
                addActionListener {
                    store.state.setState {
                        cookie.secure = isSelected
                        it
                    }
                }
            })
        }, 0.0, 4, 80)
        addComponent(JPanel().apply {
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            add(CheckBox("").apply {
                isSelected = cookie.hostOnly
                addActionListener {
                    store.state.setState {
                        cookie.hostOnly = isSelected
                        it
                    }
                }
            })
        }, 0.0, 5, 80)

        addComponent(ComboBox(CookieSameSite.entries.toTypedArray()).apply {
            selectedItem = cookie.sameSite
            addActionListener {
                store.state.setState {
                    cookie.sameSite = selectedItem as CookieSameSite
                    it
                }
            }
        }, 0.0, 6, 120)

        addComponent(JPanel().apply {
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            layout = BoxLayout(this, BoxLayout.LINE_AXIS)
            maximumSize = Dimension(100, 40)
            preferredSize = maximumSize
            add(JButton("", IconUtil.editIcon).apply {
                preferredSize = Dimension(40, 40)
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(SwingMouseListener(mousePressed = { onEdit(cookie) }))
            })
            add(JButton("", AllIcons.Actions.DeleteTagHover).apply {
                preferredSize = Dimension(40, 40)
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(SwingMouseListener(mousePressed = {
                    onDelete(parent)
                }))
            })
        }, 0.0, 7, 100)
    }
}