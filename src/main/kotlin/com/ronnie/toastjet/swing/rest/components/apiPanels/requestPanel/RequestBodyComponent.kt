package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.*
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.BoxLayout
import javax.swing.JPanel


class RequestBodyComponent(val store: RequestStore) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        tabPanel.background = background
    }

    val tabPanel = JBTabbedPane()

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        tabPanel.addTab(
            "Headers", HeaderPanel(
                headersState = store.headersState,
                theme = store.theme
            )
        )
        tabPanel.addTab(
            "Path", PathPanel(
                pathState = store.pathState,
                urlState = store.urlState,
                theme = store.theme
            )
        )
        tabPanel.addTab(
            "Params", ParamsPanel(
                urlState = store.urlState,
                theme = store.theme,
                paramsState = store.paramsState
            )
        )
        tabPanel.addTab("Body", BodyPanel(store))
        tabPanel.addTab(
            "Cookie", CookiePanel(
                cookieState = store.cookieState,
                theme = store.theme
            )
        )
        tabPanel.addTab(
            "Tests", TestPanel(
                testState = store.testState,
                appStore = store.appStore,
                theme = store.theme
            )
        )
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        add(tabPanel)
    }
}