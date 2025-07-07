package com.ronnie.toastjet.swing.socket.response

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseCookiesPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseHeadersPanel
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class MessageResPanel(val store : SocketStore) : JPanel() {
    val tabPanel = JBTabbedPane()

    fun setTheme(theme: EditorColorsManager) {
        tabPanel.foreground = theme.globalScheme.defaultForeground
        tabPanel.background = theme.globalScheme.defaultBackground
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    init {
        preferredSize = Dimension(600, preferredSize.height)
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)

        tabPanel.addTab("Headers", JPanel().apply {
            layout = BorderLayout()
            preferredSize = Dimension(600, preferredSize.height)
            maximumSize = preferredSize
            add(ResponseHeadersPanel(store.theme,store.connectResponse.getState().responseHeaders))
        })
        tabPanel.addTab(
            "Body", SocketMessagePanel(store)
        )
        tabPanel.addTab("Cookie", ResponseCookiesPanel(store.theme, store.connectResponse.getState().setCookie))
        tabPanel.addTab("Actual Request", SocketActualRequest(store))

        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)

        add(tabPanel)
        tabPanel.selectedIndex = 1
    }
}