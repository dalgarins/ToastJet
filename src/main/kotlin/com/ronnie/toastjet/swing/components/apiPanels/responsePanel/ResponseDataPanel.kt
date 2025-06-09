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

    val theme = EditorColorsManager.getInstance()

    init {
        preferredSize = Dimension(600, preferredSize.height)
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        val tabPanel = JBTabbedPane().apply {
            background = theme.globalScheme.defaultBackground
            foreground = theme.globalScheme.defaultForeground
        }

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

        add(tabPanel)
        tabPanel.selectedIndex = 1
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

}