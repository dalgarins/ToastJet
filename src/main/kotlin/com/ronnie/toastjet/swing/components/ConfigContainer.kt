package com.ronnie.toastjet.swing.components


import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.ronnie.toastjet.swing.components.configPanels.*
import com.ronnie.toastjet.swing.components.configPanels.cookiePanel.cookieContainer
import com.ronnie.toastjet.swing.store.ConfigStore
import javax.swing.BoxLayout
import javax.swing.JPanel

class ConfigContainer(store: ConfigStore) : JBScrollPane(JPanel().apply {

    layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
    val tabPanel = JBTabbedPane().apply {
        addTab("Configurations", ConfigPanel(store))
        addTab("Vars", VariablePanel(store))
        addTab("Environment", EnvPanel(store))
        addTab("Functions", FunctionPanel(store))
        addTab("Description", DescriptionPanel(store))
        addTab("Swagger Config", SwaggerPanel(store))
        addTab("Cookies", cookieContainer(store))
    }
    add(tabPanel)
}) {
    init {
        verticalScrollBarPolicy = VERTICAL_SCROLLBAR_AS_NEEDED
        horizontalScrollBarPolicy = HORIZONTAL_SCROLLBAR_AS_NEEDED
    }

}