package com.ronnie.toastjet.swing.components.configPanels.cookiePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.ConfigStore
import javax.swing.*

fun cookieContainer(store: ConfigStore): JScrollPane {

    var originalCookieLength = store.state.getState().cookie.size

    val container = JPanel()
    listCookie(container = container, store = store)

    store.state.addEffect {
        println("Are we called $originalCookieLength ${it.cookie.size}")
        if(originalCookieLength != it.cookie.size){
            originalCookieLength = it.cookie.size
            container.removeAll()
            listCookie(container = container, store = store)
            container.revalidate()
            container.repaint()
        }
    }

    return JBScrollPane(container).apply {
        val theme = EditorColorsManager.getInstance().globalScheme
        background = theme.defaultBackground
        foreground = theme.defaultForeground
        verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        border = JBUI.Borders.empty()
    }
}

