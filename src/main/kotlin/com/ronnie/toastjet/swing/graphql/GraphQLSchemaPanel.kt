package com.ronnie.toastjet.swing.graphql

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.engine.apiEngine.graphql.GraphQLClient
import com.ronnie.toastjet.model.data.AllGraphQLSchemaInfo
import com.ronnie.toastjet.swing.graphql.graphQLRequest.createSchemaTree
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.GraphQLStore
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class GraphQLSchemaPanel(val store: GraphQLStore, val configStore: ConfigStore) : JPanel() {
    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
    }

    var schemaTree: JComponent? = null

    fun addTree(s: AllGraphQLSchemaInfo) {
        if (schemaTree != null) remove(schemaTree)
        schemaTree = createSchemaTree(store)
        println("Add tree should be called $schemaTree ${store.graphQLSchema}")
        add(schemaTree!!, BorderLayout.CENTER)
        repaint()
        revalidate()
    }

    init {
        layout = BorderLayout()
        add(JButton("Fetch Schema").apply {
            addActionListener {
                val schema = GraphQLClient.fetchAllGraphQLSchema(store.getCurrentGraphQLFromStates())
                store.graphQLSchema.setState(schema ?: AllGraphQLSchemaInfo())
            }
        }, BorderLayout.NORTH)
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        addTree(store.graphQLSchema.getState())
        store.graphQLSchema.addListener(this::addTree)
    }
}