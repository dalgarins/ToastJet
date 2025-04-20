package com.ronnie.toastjet.swing.components.apiPanels.requestPanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.*
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.BoxLayout
import javax.swing.JPanel


class RequestBodyComponent( store: RequestStore) : JPanel() {

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        val tabPanel = JBTabbedPane().apply {
            val theme = EditorColorsManager.getInstance()
            background = theme.globalScheme.defaultBackground
            foreground = theme.globalScheme.defaultForeground

        }

        tabPanel.addTab("Headers", HeaderPanel(store))
        tabPanel.addTab("Path", PathPanel(store))
        tabPanel.addTab("Params", ParamsPanel(store))
        tabPanel.addTab("Body", BodyPanel(store))
        tabPanel.addTab("Cookie", CookiePanel(store))
        tabPanel.addTab("Tests", TestPanel(store))

        add(tabPanel)
        val theme = EditorColorsManager.getInstance()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }
}