package com.ronnie.toastjet.swing.components.apiPanels.requestPanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.*
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.BoxLayout
import javax.swing.JPanel


class RequestBodyComponent(val store: RequestStore) : JPanel() {

    fun setTheme(theme: EditorColorsManager){
        background = theme.globalScheme.defaultBackground
        tabPanel.background = background
    }

    val tabPanel = JBTabbedPane()

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        tabPanel.addTab("Headers", HeaderPanel(store))
        tabPanel.addTab("Path", PathPanel(store))
        tabPanel.addTab("Params", ParamsPanel(store))
        tabPanel.addTab("Body", BodyPanel(store))
        tabPanel.addTab("Cookie", CookiePanel(store))
        tabPanel.addTab("Tests", TestPanel(store))
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        add(tabPanel)
    }
}