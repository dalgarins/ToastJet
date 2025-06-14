package com.ronnie.toastjet.swing.components.apiPanels

import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.RequestBodyComponent
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.RequestOptionsComponent
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.RequestUrlComponent
import com.ronnie.toastjet.swing.store.ConfigStore
import javax.swing.JPanel
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import javax.swing.*

class RequestComponent(private val store: RequestStore, configStore: ConfigStore) : JPanel() {

    fun setTheme() {
        val theme = store.theme.getState()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        preferredSize = Dimension(600, preferredSize.height)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
        add(RequestOptionsComponent(store, configStore))
        add(RequestUrlComponent(store))
        add(RequestBodyComponent(store))
        setTheme()
        store.theme.addListener { setTheme() }
    }
}





