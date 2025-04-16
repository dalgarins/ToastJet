package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.ComboBox
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.RawType
import com.ronnie.toastjet.swing.listeners.SwingMouseListener
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Cursor
import java.awt.Dimension
import javax.swing.*

class BodyTypePanel(private val store: RequestStore) : JPanel() {
    private val theme = EditorColorsManager.getInstance().globalScheme

    // Create a button group for mutually exclusive radio buttons
    private val buttonGroup = ButtonGroup()

    private val rawType = ComboBox(RawType.entries.map { it.name }.toTypedArray()).apply {
        background = theme.defaultBackground
        preferredSize = Dimension(150, 30)
        maximumSize = preferredSize
        isVisible = store.state.getState().bodyTypeState == BodyType.RAW

        addItemListener {
            val selected = RawType.valueOf(it.item.toString())
            store.state.setState { state ->
                state.rawTypeState = selected
                state
            }
        }
    }

    init {
        background = theme.defaultBackground
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        preferredSize = Dimension(Int.MAX_VALUE, 30)
        maximumSize = preferredSize
        minimumSize = preferredSize

        // Helper function to create radio button with label
        fun createBodyTypeOption(
            bodyType: BodyType,
            label: String,
            rawTypeVisibility: Boolean = false
        ): JComponent {
            return JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                background = theme.defaultBackground

                val radioButton = JRadioButton().apply {
                    background = theme.defaultBackground
                    cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                    isSelected = store.state.getState().bodyTypeState == bodyType

                    addActionListener {
                        store.state.setState { state ->
                            state.bodyTypeState = bodyType
                            state
                        }
                    }

                    // Update selection when state changes
                    store.state.addListener { state ->
                        isSelected = state.bodyTypeState == bodyType
                        if (state.bodyTypeState == bodyType && rawTypeVisibility) {
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
                        store.state.setState { state ->
                            state.bodyTypeState = bodyType
                            state
                        }
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