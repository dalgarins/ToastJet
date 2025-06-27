package com.ronnie.toastjet.swing.graphql.graphQLResponse

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.model.data.GraphQLResponseData
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseBodyPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseCookiesPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseHeadersPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.responseData.ResponseTestPanel
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class GraphQLDataPanel(
    theme: StateHolder<EditorColorsManager>,
    responseData: StateHolder<GraphQLResponseData>,
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
            add(ResponseHeadersPanel(theme, responseData))
        })
        tabPanel.addTab(
            "Body", ResponseBodyPanel(
                theme,
                responseData,
                appStore
            )
        )
        tabPanel.addTab("Cookie", ResponseCookiesPanel(theme, responseData))
        tabPanel.addTab("Request", GraphQLResReqBodyPanel(theme, appStore, responseData))
        tabPanel.addTab("Tests", ResponseTestPanel(theme, responseData))

        setTheme(theme.getState())
        theme.addListener(this::setTheme)

        add(tabPanel)
        tabPanel.selectedIndex = 1

    }

}