package com.ronnie.toastjet.swing.graphql.graphQLResponse

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel.ResponseStatsPanel
import com.ronnie.toastjet.swing.store.GraphQLStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel

class GraphQLResponseInvoked(store: GraphQLStore) : JPanel() {

    val panel = JPanel(BorderLayout()).apply {
        preferredSize = Dimension(900, preferredSize.height)
        add(ResponseStatsPanel(store.theme,store.response), BorderLayout.NORTH)
        add(GraphQLDataPanel(
            store.theme,
            store.response,
            store.appStore
        ), BorderLayout.CENTER)
    }

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        panel.background = theme.globalScheme.defaultBackground
        panel.foreground = theme.globalScheme.defaultForeground
    }

    init {
        layout = BorderLayout()
        add(panel, BorderLayout.CENTER)
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }
}



