package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.RawType
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.*
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody.rawBody.*
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.*

class BodyPanel(val store: RequestStore) : JPanel() {

    private var oldState: BodyType? = null
    private var rawState: RawType? = null
    private var bodyComponent: JComponent = JsonEditor(store)

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    private fun renderBody(data: RequestData) {
        if (data.bodyTypeState == BodyType.RAW && rawState != data.rawTypeState) {
            remove(bodyComponent)
            oldState = data.bodyTypeState
            rawState = data.rawTypeState
            when (data.rawTypeState) {
                RawType.JSON -> bodyComponent = JsonEditor(store)
                RawType.XML -> {
                    bodyComponent = XMLEditor(store)
                }

                RawType.TEXT -> {
                    bodyComponent = TextEditor(store)
                }

                RawType.HTML -> {
                    bodyComponent = HTMLEditor(store)
                }

                RawType.JS -> {
                    bodyComponent = JsEditor(store)
                }

                RawType.GraphQL -> {
                    bodyComponent = GraphQLEditor(store)
                }
            }
            add(bodyComponent)
            repaint()
            revalidate()
        } else if (oldState != data.bodyTypeState) {
            oldState = data.bodyTypeState
            remove(bodyComponent)
            when (data.bodyTypeState) {
                BodyType.None -> bodyComponent = NonePanel(store.theme)
                BodyType.FormData -> bodyComponent = FormDataPanel(store)
                BodyType.URLEncoded -> bodyComponent = UrlEncodedPanel(store)
                BodyType.Binary -> bodyComponent = BinaryPanel(store)
                BodyType.RAW -> {
                    when (data.rawTypeState) {
                        RawType.JSON -> {
                            bodyComponent = JsonEditor(store)
                            rawState = null
                        }

                        RawType.XML -> {
                            bodyComponent = XMLEditor(store)
                            rawState = null
                        }

                        RawType.TEXT, RawType.JS -> {
                            bodyComponent = TextEditor(store)
                            rawState = null
                        }

                        RawType.HTML -> {
                            bodyComponent = HTMLEditor(store)
                            rawState = null
                        }

                        RawType.GraphQL -> {
                            rawState = null
                        }
                    }
                }
            }
            add(bodyComponent)
            repaint()
            revalidate()
        }
    }

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        add(BodyTypePanel(store))
        renderBody(store.getCurrentRequestDataFromStates())
        store.bodyTypeState.addListener { renderBody(store.getCurrentRequestDataFromStates()) }
        store.rawTypeState.addListener { renderBody(store.getCurrentRequestDataFromStates()) }
    }
}