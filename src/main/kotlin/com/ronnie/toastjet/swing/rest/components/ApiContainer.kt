package com.ronnie.toastjet.swing.rest.components

import java.awt.BorderLayout
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.rest.components.apiPanels.ReqResComponent
import com.ronnie.toastjet.swing.rest.components.apiPanels.RequestNamePanel
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import javax.swing.*

class ApiContainer(private val store: RequestStore,private val configStore: ConfigStore) : JPanel(BorderLayout()) {

    private val container = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        maximumSize = Dimension(Int.MAX_VALUE,Int.MAX_VALUE)
        val theme = store.theme.getState()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    init {
        border = JBUI.Borders.empty()
        renderRequests()
        val scrollPane = JBScrollPane(container).apply {
            verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        }
        add(scrollPane, BorderLayout.CENTER)

        store.theme.addListener {
            val theme = store.theme.getState()
            container.background = theme.globalScheme.defaultBackground
            container.foreground = theme.globalScheme.defaultForeground
        }
    }

    private fun renderRequests(){
        container.removeAll()
        container.add(RequestNamePanel(store))
        container.add(Box.createVerticalStrut(10))
        container.add(ReqResComponent(store, configStore))
        container.repaint()
        container.revalidate()
    }

}

