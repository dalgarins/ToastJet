package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.enums.CookieSameSite
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.*
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class CookiePanel(private val store: RequestStore) : JPanel(BorderLayout()) {

    private val cellBorder = MatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY)
    private val theme = EditorColorsManager.getInstance()

    companion object {
        private const val BOOLEAN_WIDTH = 30
        private const val SELECT_WIDTH = 100
        private const val DELETE_COL_WIDTH = 30
    }

    private fun getHeader(title: String, width: Int? = null): JComponent {
        return JLabel(title).apply {
            preferredSize = Dimension(width ?: 0, 30)
            horizontalAlignment = SwingConstants.CENTER
            font = Font(font.name, Font.PLAIN, 15)
            foreground = JBColor.DARK_GRAY
        }
    }

    private var enabledCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(getHeader(" "))
    }

    private var keyCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(getHeader("Key"))
    }

    private var valueCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(getHeader("Value"))
    }

    private var httpOnly = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(getHeader("HttpOnly", 100))
    }

    private var secure = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(getHeader("Secure", 100))
    }


    private var sameSite = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(getHeader("SameSite", 100))
    }

    private var deleteCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        add(getHeader(" "))
    }

    private fun constructFormData() {
        val cookie = store.state.getState().cookie
        cookie.forEachIndexed { index, _ -> addRow(index) }
        if (cookie.isEmpty()) addRow(0)
        else {
            val lastCookie = cookie.last()
            if ((lastCookie.key.trim().isNotEmpty() || lastCookie.value.trim().isNotEmpty())) {
                addRow(cookie.size)
            }
        }
    }

    private var formDataComponent = JPanel().apply {
        layout = GridBagLayout()
        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.emptyInsets()
        }
        border = JBUI.Borders.compound(JBUI.Borders.emptyTop(5), LineBorder(JBColor.LIGHT_GRAY))
        background = theme.globalScheme.defaultBackground

        add(enabledCol, gbcLayout(gbc, x = 0, y = 0, weightX = 0.0001))
        add(keyCol, gbcLayout(gbc, x = 1, y = 0, weightX = 0.5))
        add(valueCol, gbcLayout(gbc, x = 2, y = 0, weightX = 0.5))
        add(httpOnly, gbcLayout(gbc, x = 3, y = 0, weightX = 0.0001))
        add(secure, gbcLayout(gbc, x = 4, y = 0, weightX = 0.0001))
        add(sameSite, gbcLayout(gbc, x = 5, y = 0, weightX = 0.0001))
        add(deleteCol, gbcLayout(gbc, x = 6, y = 0, weightX = 0.0001))
        constructFormData()
    }

    private fun restore() {
        enabledCol.removeAll()
        enabledCol.add(getHeader(" "))
        keyCol.removeAll()
        keyCol.add(getHeader("Key"))
        valueCol.removeAll()
        valueCol.add(getHeader("Value"))
        httpOnly.removeAll()
        httpOnly.add(getHeader("HttpOnly", 100))
        secure.removeAll()
        secure.add(getHeader("Secure", 100))
        sameSite.removeAll()
        sameSite.add(getHeader("SameSite", 100))
        deleteCol.removeAll()
        deleteCol.add(getHeader(" "))
        constructFormData()
        repaint()
        revalidate()
    }

    private val contentPanel = JPanel(BorderLayout()).apply {
        background = EditorColorsManager.getInstance().globalScheme.defaultBackground
        add(formDataComponent, BorderLayout.NORTH)
    }

    private val scrollPane = JBScrollPane(contentPanel).apply {
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        border = BorderFactory.createEmptyBorder()
    }

    init {
        background = EditorColorsManager.getInstance().globalScheme.defaultBackground
        add(scrollPane, BorderLayout.CENTER)
    }

    private fun addRow(i: Int) {
        val cookie = store.state.getState().cookie.getOrNull(i)

        enabledCol.add(
            centeredCell(
                JCheckBox().apply {
                    isSelected = cookie?.enabled ?: true
                    addChangeListener {
                        store.state.setState {
                            if (it.cookie.size > i) {
                                it.cookie[i].enabled = isSelected
                            } else {
                                it.cookie.add(CookieData())
                                addRow(it.cookie.size)
                            }
                            it
                        }
                    }
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(BOOLEAN_WIDTH, 20)
                    maximumSize = preferredSize
                    horizontalAlignment = SwingConstants.CENTER
                }
            )
        )

        keyCol.add(
            centeredCell(
                JBTextField().apply {
                    text = cookie?.key ?: ""
                    border = JBUI.Borders.empty()
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(0, 30)

                    document.addDocumentListener(object : DocumentListener {
                        override fun insertUpdate(e: DocumentEvent) {
                            updateFormData()
                        }

                        override fun removeUpdate(e: DocumentEvent) {
                            updateFormData()
                        }

                        override fun changedUpdate(e: DocumentEvent) {
                            updateFormData()
                        }

                        private fun updateFormData() {
                            store.state.setState {
                                if (it.cookie.size > i) {
                                    it.cookie[i].key = this@apply.text
                                } else {
                                    if(this@apply.text.isNotEmpty()) {
                                        it.cookie.add(CookieData())
                                        addRow(it.cookie.size)
                                    }
                                }
                                it
                            }
                        }
                    })
                }
            )
        )

        valueCol.add(
            centeredCell(
                JBTextField().apply {
                    text = cookie?.value ?: ""
                    border = JBUI.Borders.empty()
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(0, 30)

                    document.addDocumentListener(object : DocumentListener {
                        override fun insertUpdate(e: DocumentEvent) {
                            updateFormData()
                        }

                        override fun removeUpdate(e: DocumentEvent) {
                            updateFormData()
                        }

                        override fun changedUpdate(e: DocumentEvent) {
                            updateFormData()
                        }

                        private fun updateFormData() {
                            store.state.setState {
                                if (it.cookie.size > i) {
                                    it.cookie[i].value = this@apply.text
                                } else {
                                    if (this@apply.text.isNotEmpty()) {
                                        it.cookie.add(CookieData())
                                        addRow(it.cookie.size)
                                    }
                                }
                                it
                            }
                        }
                    })
                }
            )
        )

        httpOnly.add(
            centeredCell(
                JCheckBox().apply {
                    isSelected = cookie?.httpOnly ?: false
                    addChangeListener {
                        store.state.setState {
                            if (it.cookie.size > i) {
                                it.cookie[i].httpOnly = isSelected
                            }
                            it
                        }
                    }
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(BOOLEAN_WIDTH, 20)
                    maximumSize = preferredSize
                    horizontalAlignment = SwingConstants.CENTER
                }
            )
        )


        secure.add(
            centeredCell(
                JCheckBox().apply {
                    isSelected = cookie?.secure ?: true
                    addChangeListener {
                        store.state.setState {
                            if (it.cookie.size > i) {
                                it.cookie[i].secure = isSelected
                            }
                            it
                        }
                    }
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(BOOLEAN_WIDTH, 20)
                    maximumSize = preferredSize
                    horizontalAlignment = SwingConstants.CENTER
                }
            )
        )


        sameSite.add(
            centeredCell(
                ComboBox(CookieSameSite.entries.toTypedArray()).apply {
                    selectedItem = cookie?.sameSite?.name ?: CookieSameSite.None.name
                    preferredSize = Dimension(SELECT_WIDTH, 30)
                    maximumSize = Dimension(SELECT_WIDTH, 35)
                    background = theme.globalScheme.defaultBackground
                    border = JBUI.Borders.empty()

                    addItemListener { e ->
                        if (e.stateChange == ItemEvent.SELECTED) {
                            val selected = e.item as? String ?: return@addItemListener

                            store.state.setState {
                                sameSite.removeAll()

                                val sameSiteValue = CookieSameSite.valueOf(selected)
                                if (it.cookie.size > i) {
                                    println("The selected item is $selected")
                                    it.cookie[i].sameSite = sameSiteValue
                                }
                                it
                            }
                        }
                    }
                }
            )
        )


        deleteCol.add(
            centeredCell(
                JLabel("x").apply {
                    font = Font(font.name, font.style, 20)
                    foreground = JBColor.RED
                    horizontalAlignment = SwingConstants.CENTER
                    preferredSize = Dimension(DELETE_COL_WIDTH, 30)
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    addMouseListener(
                        SwingMouseListener(
                            mousePressed = {
                                store.state.setState {
                                    if (i < store.state.getState().cookie.size) {
                                        it.cookie.removeAt(i)
                                    }
                                    it
                                }
                                restore()
                            }
                        )
                    )
                }
            )
        )
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
