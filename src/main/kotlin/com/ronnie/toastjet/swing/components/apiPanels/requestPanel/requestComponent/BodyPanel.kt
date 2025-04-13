package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.RawType
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.requestBody.*
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.*

class BodyPanel(val store: RequestStore) : JPanel() {

    private var bodyComponent: JComponent = JsonPanel()

    private fun renderBody(data: RequestData) {
        remove(bodyComponent)
        when (data.bodyTypeState) {
            BodyType.None -> bodyComponent = NonePanel()
            BodyType.FormData -> bodyComponent  = FormDataPanel(store)

            BodyType.URLEncoded -> bodyComponent = UrlEncodedPanel()
            BodyType.Binary -> bodyComponent = BinaryPanel()
            BodyType.RAW -> {
                when (data.rawTypeState) {
                    RawType.JSON -> {

                    }

                    RawType.XML -> {

                    }

                    RawType.TEXT -> {

                    }

                    RawType.HTML -> {

                    }

                    RawType.JS -> {

                    }

                    RawType.GraphQL -> {

                    }
                }
            }
        }
        add(bodyComponent)
        repaint()
        revalidate()
    }

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
        add(BodyTypePanel(store))
        renderBody(store.state.getState())
        store.state.addListener(this::renderBody)
    }
}