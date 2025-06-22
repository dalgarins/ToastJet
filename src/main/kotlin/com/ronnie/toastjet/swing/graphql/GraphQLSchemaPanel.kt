package com.ronnie.toastjet.swing.graphql

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.engine.apiEngine.rest.client.OkClient
import com.ronnie.toastjet.swing.graphql.graphQLRequest.createSchemaTree
import com.ronnie.toastjet.swing.store.GraphQLStore
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class GraphQLSchemaPanel(val store: GraphQLStore) : JPanel() {
    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
    }

    var schemaTree: JComponent? = null

    fun addTree() {
        if (schemaTree != null) remove(schemaTree)
        schemaTree = createSchemaTree(store)
        add(schemaTree)
    }

    init {
        add(JButton("Fetch Schema").apply {
            addActionListener {
                val schema = OkClient.fetchGraphQLSchema(store.getCurrentGraphQLFromStates())
                store.graphQLSchema.setState(schema)
            }
        })
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
        addTree()
        store.graphQLSchema.addListener {
            addTree()
        }
    }
}