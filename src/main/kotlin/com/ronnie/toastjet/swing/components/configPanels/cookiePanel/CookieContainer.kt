package com.ronnie.toastjet.swing.components.configPanels.cookiePanel

import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.*

fun cookieContainer(store: ConfigStore, requestStore: RequestStore): JScrollPane {

    var originalCookieLength = store.state.getState().cookie.size

    val container = JPanel()
    listCookie(container = container, store = store)

    requestStore.response.addEffect {
        container.removeAll()
        listCookie(container = container, store = store)
        container.repaint()
        container.revalidate()
    }

    store.state.addEffect {
        if (originalCookieLength != it.cookie.size) {
            originalCookieLength = it.cookie.size
            container.removeAll()
            listCookie(container = container, store = store)
            container.revalidate()
            container.repaint()
        }
    }

    return JBScrollPane(container).apply {
        background = store.theme.getState().globalScheme.defaultBackground
        foreground = store.theme.getState().globalScheme.defaultForeground
        store.theme.addListener {
            background = it.globalScheme.defaultBackground
            foreground = it.globalScheme.defaultForeground
        }
        verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        border = JBUI.Borders.empty()
    }
}

