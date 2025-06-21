package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.RawType
import com.ronnie.toastjet.swing.rest.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Cursor
import java.awt.Dimension
import javax.swing.*

class BodyTypePanel(private val store: RequestStore) : JPanel() {

    private val buttonGroup = ButtonGroup()

    fun setTheme(theme: EditorColorsManager) {
        rawType.background = theme.globalScheme.defaultBackground
        background = rawType.background
    }

    private val rawType = ComboBox(RawType.entries.map { it.name }.toTypedArray()).apply {
        preferredSize = Dimension(150, 30)
        maximumSize = preferredSize
        isVisible = store.bodyTypeState.getState() == BodyType.RAW
        selectedItem = store.rawTypeState.getState().name
        addItemListener {
            val selected = RawType.valueOf(it.item.toString())
            store.rawTypeState.setState(selected)
        }
    }

    init {
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        preferredSize = Dimension(Int.MAX_VALUE, 30)
        maximumSize = preferredSize
        minimumSize = preferredSize

        fun createBodyTypeOption(
            bodyType: BodyType,
            label: String,
            rawTypeVisibility: Boolean = false
        ): JComponent {
            return JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                background = store.theme.getState().globalScheme.defaultBackground
                store.theme.addListener { background = it.globalScheme.defaultBackground }

                val radioButton = JRadioButton().apply {
                    background = store.theme.getState().globalScheme.defaultBackground
                    store.theme.addListener { background = it.globalScheme.defaultBackground }
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    isSelected = store.bodyTypeState.getState() == bodyType

                    addActionListener {
                        store.bodyTypeState.setState(bodyType)
                    }

                    store.bodyTypeState.addListener { state ->
                        isSelected = state == bodyType
                        if (state == bodyType && rawTypeVisibility) {
                            rawType.isVisible = true
                        } else if (rawTypeVisibility) {
                            rawType.isVisible = false
                        }
                    }
                }

                buttonGroup.add(radioButton)
                add(radioButton)
                add(JLabel(label).apply {
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    addMouseListener(SwingMouseListener(mousePressed = {
                        radioButton.isSelected = true
                        store.bodyTypeState.setState(bodyType)
                    }))
                })
                add(Box.createHorizontalStrut(5))
            }
        }

        // Add all body type options
        add(createBodyTypeOption(BodyType.None, "None"))
        add(createBodyTypeOption(BodyType.FormData, "Form-Data"))
        add(createBodyTypeOption(BodyType.URLEncoded, "URL-Encoded"))
        add(createBodyTypeOption(BodyType.Binary, "Binary"))
        add(createBodyTypeOption(BodyType.RAW, "Raw", true))
        add(Box.createHorizontalStrut(10))
        add(rawType)
    }
}