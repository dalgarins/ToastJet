package com.ronnie.toastjet.swing.graphql.graphQLResponse

import com.ronnie.toastjet.swing.store.GraphQLStore
import javax.swing.JLabel
import javax.swing.JPanel

class GraphQLResponsePanel(val store: GraphQLStore) : JPanel() {
    init {
        add(JLabel("This is response"))
    }
}