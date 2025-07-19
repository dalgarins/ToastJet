package com.ronnie.toastjet.swing.socket.request

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.model.enums.SocketType
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.CookiePanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.HeaderPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.ParamsPanel
import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.PathPanel
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.SocketStore
import javax.swing.BoxLayout
import javax.swing.JPanel

class SocketDataComponent(
    val store: SocketStore,
    val configStore: ConfigStore
) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        tabPanel.background = background
    }

    val eventTabs = HeaderPanel(store.eventList, store.theme, listOf(" ", "Event", "Description"))

    val tabPanel = JBTabbedPane()

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        tabPanel.addTab("Message", MessageReqPanel(store))
        tabPanel.addTab("Headers", HeaderPanel(store.headersState, store.theme))
        tabPanel.addTab("Path", PathPanel(store.pathState, store.urlState, store.theme))
        tabPanel.addTab("Params", ParamsPanel(store.paramsState, store.urlState, store.theme))
        tabPanel.addTab(
            "Cookie", CookiePanel(
                store.cookieState,
                store.theme
            )
        )
        if (store.socketState.getState() == SocketType.SocketIO) {
            tabPanel.addTab("Event", eventTabs)
        }
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        add(tabPanel)
        store.socketState.addListener(this::updateTabPanel)
    }

    fun updateTabPanel(socketType: SocketType) {
        if (socketType == SocketType.SocketIO) {
            if (!tabPanel.components.contains(eventTabs)) {
                tabPanel.addTab("Events", eventTabs)
            }
        } else {
            if (tabPanel.components.contains(eventTabs)) {
                tabPanel.remove(eventTabs)
            }
        }
    }

}