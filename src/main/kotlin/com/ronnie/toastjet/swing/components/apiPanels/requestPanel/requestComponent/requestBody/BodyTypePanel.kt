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

class BodyTypePanel(private val store: RequestStore): JPanel() {
    val theme = EditorColorsManager.getInstance().globalScheme
    private val rawType = ComboBox(arrayOf("JSON", "XML", "TEXT", "HTML", "Graph QL", "JavaScript")).apply {
        background = theme.defaultBackground
        selectedItem = "JSON"
        preferredSize = Dimension(150, 30)
        maximumSize = preferredSize
        isVisible = store.state.getState().bodyTypeState == BodyType.RAW
    }
    init{
        background = theme.defaultBackground
        rawType.addActionListener {
            when (it.actionCommand) {
                "JSON" -> store.state.setState {
                    it.rawTypeState = RawType.JSON
                    it
                }

                "XML" -> store.state.setState() {
                    it.rawTypeState = RawType.XML
                    it
                }

                "TEXT" -> store.state.setState {
                    it.rawTypeState = RawType.TEXT
                    it
                }

                "HTML" -> store.state.setState {
                    it.rawTypeState = RawType.HTML
                    it
                }

                "Graph QL" -> store.state.setState {
                    it.rawTypeState = RawType.GraphQL
                    it
                }

                "JavaScript" -> store.state.setState {
                    it.rawTypeState = RawType.JS
                    it
                }
            }
        }
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        preferredSize = Dimension(Int.MAX_VALUE, 30)
        maximumSize = preferredSize
        minimumSize = preferredSize

        //None Body Type
        add(JRadioButton().apply {
            background = theme.defaultBackground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener {
                store.state.setState {
                    it.bodyTypeState = BodyType.None
                    it
                }
            }
            isSelected = store.state.getState().bodyTypeState == BodyType.None
            store.state.addListener {
                this.isSelected = it.bodyTypeState == BodyType.None
                this.repaint()
                this.revalidate()
            }
        })
        add(JLabel("None").apply {
            background = theme.defaultBackground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {
                        store.state.setState {
                            it.bodyTypeState = BodyType.None
                            it
                        }
                    }
                )
            )
        })
        add(Box.createHorizontalStrut(5))

        //FormData body type
        add(JRadioButton().apply {
            background = theme.defaultBackground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener {
                store.state.setState {
                    it.bodyTypeState = BodyType.FormData
                    it
                }
            }
            isSelected = store.state.getState().bodyTypeState == BodyType.FormData
            store.state.addListener {
                this.isSelected = it.bodyTypeState == BodyType.FormData
                this.repaint()
                this.revalidate()
            }
        })
        add(JLabel("Form-Data").apply {
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {
                        store.state.setState {
                            it.bodyTypeState = BodyType.FormData
                            it
                        }
                    }
                ))
        })
        add(Box.createHorizontalStrut(5))

        //UrlEncoded body type
        add(JRadioButton().apply {
            background = theme.defaultBackground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener {
                store.state.setState {
                    it.bodyTypeState = BodyType.URLEncoded
                    it
                }
            }
            isSelected = store.state.getState().bodyTypeState == BodyType.URLEncoded
            store.state.addListener {
                this.isSelected = it.bodyTypeState == BodyType.URLEncoded
                this.repaint()
                this.revalidate()
            }
        })
        add(JLabel("URL-Encoded").apply {
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {   store.state.setState {
                        it.bodyTypeState = BodyType.URLEncoded
                        it
                    } }
                ))
        })
        add(Box.createHorizontalStrut(5))

        //Binary body type
        add(JRadioButton().apply {
            background = theme.defaultBackground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener {
                store.state.setState {
                    it.bodyTypeState = BodyType.Binary
                    it
                }
            }
            isSelected = store.state.getState().bodyTypeState == BodyType.Binary
            store.state.addListener {
                this.isSelected = it.bodyTypeState == BodyType.Binary
                this.repaint()
                this.revalidate()
            }
        })
        add(JLabel("Binary").apply {
            background = theme.defaultBackground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {  store.state.setState {
                        it.bodyTypeState = BodyType.Binary
                        it
                    } }
                ))
        })
        add(Box.createHorizontalStrut(5))

        add(JRadioButton().apply {
            background = theme.defaultBackground
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            isSelected = store.state.getState().bodyTypeState == BodyType.RAW
            addActionListener {
                store.state.setState{
                    it.bodyTypeState = BodyType.RAW
                    it
                }
            }
            store.state.addListener {
                this.isSelected =  it.bodyTypeState == BodyType.RAW
                if (it.bodyTypeState == BodyType.RAW) {
                    rawType.isVisible = true
                    rawType.repaint()
                    rawType.revalidate()
                } else {
                    rawType.isVisible = false
                    rawType.repaint()
                    rawType.revalidate()
                }
                this.repaint()
                this.revalidate()
            }
        })
        add(JLabel("Raw").apply {
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addMouseListener(
                SwingMouseListener(
                    mousePressed = {   store.state.setState {
                        it.bodyTypeState = BodyType.RAW
                        it
                    } }
                ))
        })
        add(Box.createHorizontalStrut(10))

        add(rawType)
    }
}