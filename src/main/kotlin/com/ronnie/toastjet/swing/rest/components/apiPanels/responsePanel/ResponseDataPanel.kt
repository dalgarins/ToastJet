package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.model.data.RestResponseData
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseBodyPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseCookiesPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseHeadersPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseRequestPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseTestPanel
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class ResponseDataPanel(
    theme: StateHolder<EditorColorsManager>,
    responseData: StateHolder<RestResponseData>,
    appStore: AppStore
) : JPanel() {

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
            add(ResponseHeadersPanel(theme, responseData.getState().responseHeaders))
        })
        tabPanel.addTab(
            "Body", ResponseBodyPanel(
                theme,
                responseData,
                appStore
            )
        )
        tabPanel.addTab("Cookie", ResponseCookiesPanel(theme, responseData.getState().setCookie))
        tabPanel.addTab("Request", ResponseRequestPanel(theme, responseData, appStore))
        tabPanel.addTab("Tests", ResponseTestPanel(theme, responseData))

        setTheme(theme.getState())
        theme.addListener(this::setTheme)

        add(tabPanel)
        tabPanel.selectedIndex = 1

    }

}