package com.ronnie.toastjet.swing.components.configPanels

import com.intellij.lang.Language
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.LanguageTextField
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.*
import javax.swing.*


class ConfigPanel(store: ConfigStore) : JPanel(BorderLayout()) {


    private fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
        generalSettings.foreground = foreground
        generalSettings.background = background
        proxySettings.foreground = foreground
        proxySettings.background = background
        sshSettings.foreground = foreground
        sshSettings.background = background
        settingType.foreground = foreground
        settingType.background = background
    }

    private val generalSettings = JPanel().apply {
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(5)
            anchor = GridBagConstraints.NORTHWEST
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            weighty = 0.0
        }
        val md = Language.findLanguageByID("Markdown") ?: PlainTextLanguage.INSTANCE
        add(JLabel("Base Url : "), gbcLayout(gbc, 0, 0, 1.0))
        add(LanguageTextField(md, store.appState.project, store.state.getState().baseDomain, true).apply {
            addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    store.state.setState {
                        it.baseDomain = event.document.text
                        it
                    }

                }
            })
        }, gbcLayout(gbc, 1, 0, 14.0))
        add(JLabel("Name"), gbcLayout(gbc, 0, 1, 1.0))
        add(LanguageTextField(md, store.appState.project, "", true).apply {
            addDocumentListener(object : DocumentListener {
                override fun documentChanged(event: DocumentEvent) {
                    store.state.setState {
                        it.baseDomain = event.document.text
                        it
                    }
                }
            })
        }, gbcLayout(gbc, 1, 1, 14.0))

        gbc.gridy = 2
        gbc.weighty = 1.0
        add(Box.createVerticalGlue(), gbc)
    }

    private val proxySettings = JPanel().apply {
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(5)
            anchor = GridBagConstraints.NORTHWEST
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            weighty = 0.0
        }
        add(JLabel("Enable Proxy : "), gbcLayout(gbc, 0, 0, 1.0))
        add(JCheckBox().apply {
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            addActionListener {
                store.state.setState {
                    it.enableProxy = isSelected
                    it
                }
            }
        }, gbcLayout(gbc, 1, 0, 11.0))
        add(JLabel("Address : "), gbcLayout(gbc, 0, 1, 1.0))
        add(JTextField().apply {
            addActionListener {
                store.state.setState {
                    it.proxyAddress = this.text
                    it
                }
            }
        }, gbcLayout(gbc, 1, 1, 14.0))
        add(JLabel("Username : "), gbcLayout(gbc, 0, 2, 1.0))
        add(JTextField().apply {
            addActionListener {
                store.state.setState {
                    it.proxyUsername = text
                    it
                }
            }
        }, gbcLayout(gbc, 1, 2, 14.0))
        add(JLabel("Password : "), gbcLayout(gbc, 0, 3, 1.0))
        add(JTextField().apply {
            addActionListener {
                store.state.setState {
                    it.proxyPassword = text
                    it
                }
            }
        }, gbcLayout(gbc, 1, 3, 14.0))

        gbc.gridy = 4
        gbc.weighty = 1.0
        add(Box.createVerticalGlue(), gbc)
    }

    private val sshSettings = JPanel().apply {
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            insets = JBUI.insets(5)
            anchor = GridBagConstraints.NORTHWEST
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
            weighty = 0.0
        }
        add(JLabel("Enable SSH : "), gbcLayout(gbc, 0, 0, 1.0))
        add(JCheckBox().apply {
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            addActionListener {
                store.state.setState {
                    it.enableSsh = isSelected
                    it
                }
            }
        }, gbcLayout(gbc, 1, 0, 14.0))
        add(JLabel("Address : "), gbcLayout(gbc, 0, 1, 1.0))
        add(JTextField().apply {
            store.state.setState {
                it.sshAddress = text
                it
            }
        }, gbcLayout(gbc, 1, 1, 14.0))
        add(JLabel("Port : "), gbcLayout(gbc, 0, 2, 1.0))
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            background = store.theme.getState().globalScheme.defaultBackground
            foreground = store.theme.getState().globalScheme.defaultForeground
            store.theme.addListener {
                background = it.globalScheme.defaultBackground
                foreground = it.globalScheme.defaultForeground
            }
            add(JSpinner(SpinnerNumberModel()).apply {
                store.state.setState {
                    it.sshPort = value as Int
                    it
                }
            })
        }, gbcLayout(gbc, 1, 2, 14.0))
        add(JLabel("Username : "), gbcLayout(gbc, 0, 3, 1.0))
        add(JTextField().apply {
            store.state.setState {
                it.sshUsername = text
                it
            }
        }, gbcLayout(gbc, 1, 3, 14.0))
        add(JLabel("Password : "), gbcLayout(gbc, 0, 4, 1.0))
        add(JTextField().apply {
            store.state.setState {
                it.sshPassword = text
                it
            }
        }, gbcLayout(gbc, 1, 4, 14.0))
        gbc.gridy = 5
        gbc.weighty = 1.0
        add(JPanel(), gbc)
    }

    private val settingType = ComboBox(arrayOf("General Settings", "Proxy Settings", "SSH Settings")).apply {
        addItemListener {
            println("Are we called ${it.item}")
            this@ConfigPanel.remove(generalSettings)
            this@ConfigPanel.remove(proxySettings)
            this@ConfigPanel.remove(sshSettings)
            when (it.item) {
                "General Settings" -> {
                    this@ConfigPanel.add(generalSettings)
                }

                "Proxy Settings" -> {
                    this@ConfigPanel.add(proxySettings)
                }

                "SSH Settings" -> {
                    this@ConfigPanel.add(sshSettings)
                }
            }
            this@ConfigPanel.repaint()
            this@ConfigPanel.revalidate()
        }
    }

    init {
        border = JBUI.Borders.empty(15)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(
            JPanel().apply {
                background = store.theme.getState().globalScheme.defaultBackground
                foreground = store.theme.getState().globalScheme.defaultForeground
                store.theme.addListener {
                    background = it.globalScheme.defaultBackground
                    foreground = it.globalScheme.defaultForeground
                }
                layout = FlowLayout(FlowLayout.LEFT)
                maximumSize = Dimension(Int.MAX_VALUE, 40)
                add(JLabel("Select Configuration : ").apply {
                    font = Font(font.name, font.style, 14)
                })
                add(settingType)
            }
        )
        add(generalSettings)
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }
}
