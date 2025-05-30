package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.components.panels.VerticalLayout
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.FormData
import com.ronnie.toastjet.model.enums.FormType
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.fileUtils.loadFile
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.*
import javax.swing.*
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


class FormDataPanel(private val store: RequestStore) : JPanel(BorderLayout()) {

    private val cellBorder = MatteBorder(1, 0, 0, 0, JBColor.LIGHT_GRAY)
    private val theme = EditorColorsManager.getInstance()

    companion object {
        private const val ENABLED_COL_WIDTH = 30
        private const val TYPE_COL_WIDTH = 100
        private const val DELETE_COL_WIDTH = 30
    }

    private fun getHeader(title: String): JComponent {
        return JLabel(title).apply {
            preferredSize = Dimension(0, 30)
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

    private var typeCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        border = MatteBorder(0, 0, 0, 1, JBColor.LIGHT_GRAY)
        add(getHeader("Type"))
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

    private var deleteCol = JPanel().apply {
        layout = VerticalLayout(0)
        background = theme.globalScheme.defaultBackground
        add(getHeader(" "))
    }

    private fun constructFormData() {
        val formData = store.formDataState.getState()
        formData.forEachIndexed { index, _ -> addRow(index) }
        if (formData.isEmpty()) addRow(0)
        else {
            val lastFormData = formData.last()
            if ((lastFormData.key.trim() != "" && lastFormData.value.trim() != "")) {
                addRow(formData.size)
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
        add(typeCol, gbcLayout(gbc, x = 1, y = 0, weightX = 0.0001))
        add(keyCol, gbcLayout(gbc, x = 2, y = 0, weightX = 0.5))
        add(valueCol, gbcLayout(gbc, x = 3, y = 0, weightX = 0.5))
        add(deleteCol, gbcLayout(gbc, x = 4, y = 0, weightX = 0.0001))
        constructFormData()
    }

    private fun restore() {
        keyCol.removeAll()
        keyCol.add(getHeader("Key"))
        valueCol.removeAll()
        valueCol.add(getHeader("Value"))
        enabledCol.removeAll()
        enabledCol.add(getHeader(" "))
        deleteCol.removeAll()
        deleteCol.add(getHeader(" "))
        typeCol.removeAll()
        typeCol.add(getHeader("Type"))
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
        val formData = store.formDataState.getState().getOrNull(i)

        val valueInput = JBTextField().apply {
            text = formData?.value ?: ""
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
                    store.formDataState.setState {
                        if (it.size > i) {
                            it[i] = it[i].copy(value = this@apply.text)
                        } else {
                            it.add(FormData(false, "", this@apply.text, FormType.Text))
                            addRow(it.size)
                            contentPanel.repaint()
                            contentPanel.revalidate()
                        }
                        it
                    }
                }
            })
        }
        val buttonInput = JPanel(BorderLayout()).apply {
            background = theme.globalScheme.defaultBackground
            isOpaque = true
            border = JBUI.Borders.empty(5, 10)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            val labelText = if (
                i < store.formDataState.getState().size &&
                store.formDataState.getState()[i].type == FormType.File &&
                store.formDataState.getState()[i].value.trim().isNotEmpty()
            ) store.formDataState.getState()[i].value
            else "Select File"

            val textLabel = JLabel(labelText).apply {
                foreground = theme.globalScheme.defaultForeground
                horizontalAlignment = SwingConstants.LEFT
            }

            val iconLabel = JLabel(AllIcons.Nodes.Folder).apply {
                horizontalAlignment = SwingConstants.RIGHT
            }

            add(textLabel, BorderLayout.WEST)
            add(iconLabel, BorderLayout.EAST)

            addMouseListener(
                SwingMouseListener(
                    mousePressed = {
                        loadFile { file ->
                            textLabel.text = file.name
                            store.formDataState.setState {
                                if (it.size > i) {
                                    it[i].value = file.path
                                } else {
                                    it.add(FormData(false, "", file.name, FormType.Text))
                                    addRow(it.size)
                                }
                                it
                            }
                            repaint()
                            revalidate()
                        }
                    }
                )
            )
        }


        val valuePanel = centeredCell(
            if (
                i < store.formDataState.getState().size &&
                store.formDataState.getState()[i].type == FormType.File
            ) buttonInput
            else valueInput
        )

        enabledCol.add(
            centeredCell(
                JCheckBox().apply {
                    isSelected = formData?.enabled ?: true
                    addChangeListener {
                        store.formDataState.setState {
                            if (it.size > i) {
                                it[i].enabled = isSelected
                            } else {
                                it.add(FormData(isSelected, "", "", FormType.Text))
                                addRow(it.size)
                            }
                            it
                        }
                    }
                    background = theme.globalScheme.defaultBackground
                    preferredSize = Dimension(ENABLED_COL_WIDTH, 20)
                    maximumSize = preferredSize
                    horizontalAlignment = SwingConstants.CENTER
                }
            )
        )

        typeCol.add(
            centeredCell(
                ComboBox(arrayOf("Text", "File")).apply {
                    selectedItem = if (formData?.type == FormType.File) "File" else "Text"
                    preferredSize = Dimension(TYPE_COL_WIDTH, 30)
                    maximumSize = Dimension(TYPE_COL_WIDTH, 35)
                    background = theme.globalScheme.defaultBackground
                    border = JBUI.Borders.empty()
                    addItemListener {
                        store.formDataState.setState {
                            valuePanel.removeAll()
                            if (it.size > i) {
                                it[i].type = if (selectedItem == "Text") FormType.Text else FormType.File
                                if (selectedItem == "Text") {
                                    valuePanel.add(valueInput)
                                } else {
                                    valuePanel.add(buttonInput)
                                }
                                it[i].value = ""
                            } else {
                                it.add(
                                    FormData(
                                        true,
                                        "",
                                        "",
                                        if (selectedItem == "Text") FormType.Text else FormType.File
                                    )
                                )
                                addRow(it.size)
                            }
                            valuePanel.repaint()
                            valuePanel.revalidate()
                            it
                        }
                    }
                }
            )
        )

        keyCol.add(
            centeredCell(
                JBTextField().apply {
                    text = formData?.key ?: ""
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
                            store.formDataState.setState {
                                if (it.size > i) {
                                    it[i] = it[i].copy(key = this@apply.text)
                                } else {
                                    it.add(FormData(false, this@apply.text, "", FormType.Text))
                                    addRow(it.size)
                                }
                                it
                            }
                        }
                    })
                }
            )
        )

        valueCol.add(valuePanel)

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
                                store.formDataState.setState {
                                    if (i < store.formDataState.getState().size) {
                                        it.removeAt(i)
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
