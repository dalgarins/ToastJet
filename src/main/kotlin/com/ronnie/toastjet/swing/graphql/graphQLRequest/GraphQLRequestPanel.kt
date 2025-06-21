package com.ronnie.toastjet.swing.graphql.graphQLRequest

import com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.RequestUrlComponent
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.GraphQLStore
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JPanel

class GraphQLRequestPanel(val store: GraphQLStore, val configStore: ConfigStore) : JPanel() {

    fun setTheme() {
        val theme = store.theme.getState()
        background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
    }

    init {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        preferredSize = Dimension(600, preferredSize.height)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
        add(GraphQLOptionsComponent(store, configStore))
        add(
            RequestUrlComponent(
                urlState = store.urlState,
                paramsState = store.paramsState,
                appStore = store.appStore,
                theme = store.theme,
            )
        )
        add(GraphQLDataComponent(store))
        setTheme()
        store.theme.addListener { setTheme() }
    }
}