package com.ronnie.toastjet.swing.socket.response

import com.google.gson.Gson
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.ResponseLoading
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.ResponseNotInvoked
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.SocketStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class SocketResPanel(
    val store: SocketStore,
    val config: ConfigStore
) : JPanel() {
    val gson = Gson()

    val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun setTheme() {
        val theme = store.theme.getState()
        background = theme.globalScheme.defaultBackground
    }

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        preferredSize = Dimension(500, preferredSize.height)
        setTheme()
        isOpaque = true
        generatePanel()
        store.response.addListener { generatePanel() }
        store.theme.addListener { setTheme() }
    }

    fun generatePanel() {
        this.removeAll()
        if (store.response.getState().isBeingInvoked) {
            add(ResponseLoading(store.theme))
        } else {
            if (store.response.getState().invoked) {
                add(MessageResPanel(store))
            } else {
                add(ResponseNotInvoked(store.theme))
            }
        }
        this.repaint()
        this.revalidate()
    }
}