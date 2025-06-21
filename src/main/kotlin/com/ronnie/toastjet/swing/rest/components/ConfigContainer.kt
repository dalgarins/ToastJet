package com.ronnie.toastjet.swing.rest.components


import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.rest.components.configPanels.ConfigPanel
import com.ronnie.toastjet.swing.rest.components.configPanels.DescriptionPanel
import com.ronnie.toastjet.swing.rest.components.configPanels.EnvPanel
import com.ronnie.toastjet.swing.rest.components.configPanels.FunctionPanel
import com.ronnie.toastjet.swing.rest.components.configPanels.SwaggerPanel
import com.ronnie.toastjet.swing.rest.components.configPanels.VariablePanel
import com.ronnie.toastjet.swing.rest.components.configPanels.cookiePanel.cookieContainer
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.RequestStore
import javax.swing.BoxLayout
import javax.swing.JPanel

class ConfigContainer(store: ConfigStore,requestState: RequestStore) : JBScrollPane(JPanel().apply {

    layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
    val tabPanel = JBTabbedPane().apply {
        addTab("Configurations", ConfigPanel(store))
        addTab("Vars", VariablePanel(store))
        addTab("Environment", EnvPanel(store))
        addTab("Functions", FunctionPanel(store))
        addTab("Description", DescriptionPanel(store))
        addTab("Swagger Config", SwaggerPanel(store))
        addTab("Cookies", cookieContainer(store,requestState))
    }
    add(tabPanel)
}) {
    init {
        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED
    }

}