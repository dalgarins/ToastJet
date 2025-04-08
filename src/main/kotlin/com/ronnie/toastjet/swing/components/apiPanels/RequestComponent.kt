package com.ronnie.toastjet.swing.components.apiPanels

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.RequestBodyComponent
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.RequestOptionsComponent
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.RequestUrlComponent
import javax.swing.JPanel
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import javax.swing.*

class RequestComponent( store: RequestStore) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
        add(RequestOptionsComponent(store))
        add(RequestUrlComponent(store))
        add(RequestBodyComponent(store))
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }
}





