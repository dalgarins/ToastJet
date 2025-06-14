package com.ronnie.toastjet.swing.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData.ResponseBodyPanel
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData.ResponseCookiesPanel
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData.ResponseHeadersPanel
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData.ResponseRequestPanel
import com.ronnie.toastjet.swing.components.apiPanels.responsePanel.responseData.ResponseTestPanel
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class ResponseDataPanel(store: RequestStore) : JPanel() {

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
            add(ResponseHeadersPanel(store))
        })
        tabPanel.addTab("Body", ResponseBodyPanel(store))
        tabPanel.addTab("Cookie", ResponseCookiesPanel(store))
        tabPanel.addTab("Request", ResponseRequestPanel(store))
        tabPanel.addTab("Tests", ResponseTestPanel(store))

        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)

        add(tabPanel)
        tabPanel.selectedIndex = 1

    }

}