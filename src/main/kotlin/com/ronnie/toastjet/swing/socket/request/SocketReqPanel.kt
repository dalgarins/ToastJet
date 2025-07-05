package com.ronnie.toastjet.swing.socket.request

import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.RequestUrlComponent
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class SocketReqPanel(val store: SocketStore, configStore: ConfigStore) : JPanel() {
    fun setTheme() {
        val theme = store.theme.getState()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        preferredSize = Dimension(600, preferredSize.height)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
        add(SocketOptionsComponent(store, configStore))
        add(
            RequestUrlComponent(
                urlState = store.urlState,
                paramsState = store.paramsState,
                appStore = store.appStore,
                theme = store.theme,
            )
        )
        add(SocketDataComponent(store, configStore))
        setTheme()
        store.theme.addListener { setTheme() }
    }
}