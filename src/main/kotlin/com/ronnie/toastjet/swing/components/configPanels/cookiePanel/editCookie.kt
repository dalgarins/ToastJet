package com.ronnie.toastjet.swing.components.configPanels.cookiePanel

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.enums.CookieSameSite
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.ConfigStore
import java.awt.*
import javax.swing.*


fun editCookie(store: ConfigStore, cookie: CookieData?, container: JComponent): JComponent {
    return container.apply {
        val theme = EditorColorsManager.getInstance().globalScheme
        background = theme.defaultBackground
        foreground = theme.defaultForeground
        layout = GridBagLayout()
        maximumSize = Dimension(500, Int.MAX_VALUE)
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.insets(5)
            anchor = GridBagConstraints.NORTH
        }

        fun addLabel(text: String, row: Int, height: Int) {
            gbc.gridx = 0
            gbc.gridy = row
            gbc.weightx = 0.05
            add(JLabel(text).apply {
                verticalAlignment = SwingConstants.CENTER
                preferredSize = Dimension(100, height)
            }, gbc)
        }

        fun addInputField(component: JComponent, row: Int, height: Int) {
            gbc.gridx = 1
            gbc.gridy = row
            gbc.weightx = 0.95
            component.preferredSize = Dimension(100, height)
            add(component, gbc)
        }

        val key = JTextField().apply { text = cookie?.key ?: "" }
        addLabel("Key", 0, 40)
        addInputField(key, 0, 40)

        val value = JTextField().apply { text = cookie?.value ?: "" }
        addLabel("Value", 1, 40)
        addInputField(value, 1, 40)

        val sameSite = ComboBox(CookieSameSite.entries.toTypedArray()).apply {
            selectedItem = cookie?.sameSite ?: CookieSameSite.None
        }
        addLabel("Same Site", 2, 40)
        addInputField(sameSite, 2, 40)

        val httpOnly = JCheckBox().apply { isSelected = cookie?.httpOnly ?: false }
        addLabel("Http Only", 3, 40)
        addInputField(httpOnly, 3, 40)

        val secure = JCheckBox().apply { isSelected = cookie?.secure ?: false }
        addLabel("Secure", 4, 40)
        addInputField(secure, 4, 40)

        val hostOnly = JCheckBox().apply { isSelected = cookie?.hostOnly ?: true }
        addLabel("Host Only", 5, 40)
        addInputField(hostOnly, 5, 40)

        val pathIsDefault = JCheckBox().apply { isSelected = cookie?.pathIsDefault ?: false }
        addLabel("Path is default", 6, 40)
        addInputField(pathIsDefault, 6, 40)

        val path = JTextField().apply { text = cookie?.path ?: "" }
        addLabel("Path", 7, 40)
        addInputField(path, 7, 40)

        val domain = JTextField().apply { text = cookie?.domain ?: "" }
        addLabel("Domain", 8, 40)
        addInputField(domain, 8, 40)

        val creationTime = DateTimePanel()
        addLabel("Creation Time", 9, 80)
        addInputField(creationTime.panel, 9, 80)

        val expiryTime = DateTimePanel()
        addLabel("Expiry Time", 10, 80)
        addInputField(expiryTime.panel, 10, 80)

        gbc.gridx = 0
        gbc.gridy = 11
        gbc.gridwidth = GridBagConstraints.REMAINDER
        gbc.weighty = 1.0
        gbc.fill = GridBagConstraints.VERTICAL
        add(JPanel().apply {
            preferredSize = Dimension(Int.MAX_VALUE, preferredSize.height)
            layout = FlowLayout(FlowLayout.LEFT) // Aligns buttons to the left

            val saveButton = JButton("Save").apply {
                icon = AllIcons.Actions.MenuSaveall
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                addMouseListener(
                    SwingMouseListener(
                        mousePressed = {
                            store.state.setState {
                                if (cookie == null) {
                                    it.cookie.add(
                                        CookieData(
                                            enabled = true,
                                            key = key.text,
                                            value = value.text,
                                            path = path.text,
                                            domain = domain.text,
                                            secure = secure.isSelected,
                                            creationTime = creationTime.getJavaUtilDate(),
                                            hostOnly = hostOnly.isSelected,
                                            httpOnly = httpOnly.isSelected,
                                            sameSite = sameSite.selectedItem as CookieSameSite,
                                            pathIsDefault = pathIsDefault.isSelected,
                                            expiryTime = expiryTime.getJavaUtilDate()
                                        )
                                    )
                                } else {
                                    cookie.key = key.text
                                    cookie.value = value.text
                                    cookie.path = path.text
                                    cookie.domain = domain.text
                                    cookie.secure = secure.isSelected
                                    cookie.creationTime = creationTime.getJavaUtilDate()
                                    cookie.hostOnly = hostOnly.isSelected
                                    cookie.httpOnly = httpOnly.isSelected
                                    cookie.sameSite = sameSite.selectedItem as CookieSameSite
                                    cookie.pathIsDefault = pathIsDefault.isSelected
                                    cookie.expiryTime = expiryTime.getJavaUtilDate()
                                }
                                it
                            }
                            container.removeAll()
                            listCookie(store, container)
                            container.repaint()
                            container.revalidate()
                        }
                    ))
            }

            val cancelButton = JButton("Cancel").apply {
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                icon = AllIcons.Actions.DeleteTag
                addMouseListener(SwingMouseListener(mousePressed = {
                    container.removeAll()
                    listCookie(store, container)
                    container.repaint()
                    container.revalidate()
                }))
            }
            add(saveButton)
            add(cancelButton)
        }, gbc)
        container.repaint()
        container.revalidate()
    }
}

