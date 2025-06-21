package com.ronnie.toastjet.swing.graphql.graphQLRequest

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.CookiePanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.HeaderPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.ParamsPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.PathPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.TestPanel
import com.ronnie.toastjet.swing.store.GraphQLStore
import javax.swing.BoxLayout
import javax.swing.JPanel

class GraphQLDataComponent(val store: GraphQLStore) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        tabPanel.background = background
    }

    val tabPanel = JBTabbedPane()

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        tabPanel.addTab("Headers", HeaderPanel(store.headersState, store.theme))
        tabPanel.addTab("Path", PathPanel(store.pathState, store.urlState, store.theme))
        tabPanel.addTab("Params", ParamsPanel(store.paramsState, store.urlState, store.theme))
        tabPanel.addTab("Body", GraphQLBodyComponent(store))
        tabPanel.addTab(
            "Cookie", CookiePanel(
                store.cookieState,
                store.theme
            )
        )
        tabPanel.addTab("Tests", TestPanel(store.testState, store.appStore, store.theme))
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        add(tabPanel)
    }
}