package com.ronnie.toastjet.swing.graphql.graphQLRequest

import com.intellij.ui.components.JBScrollPane
import com.ronnie.toastjet.model.data.Member
import com.ronnie.toastjet.model.data.SchemaInfo
import com.ronnie.toastjet.swing.store.GraphQLStore
import org.jdesktop.swingx.JXTree
import java.awt.Component
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.DefaultTreeCellRenderer

class SchemaTreeCellRenderer : DefaultTreeCellRenderer() {

    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus)
        val userObject = (value as? DefaultMutableTreeNode)?.userObject
        when (userObject) {
            is SchemaInfo -> {
                text = "<html><b>${userObject.schemaName}</b>: ${userObject.schemaType}</html>"
            }

            is Member -> {
                text = "${userObject.name}: ${userObject.type}"
            }
        }
        return this
    }
}

fun createSchemaTree(store: GraphQLStore): JScrollPane {
    val rootNode = DefaultMutableTreeNode("Root")
    for (schemaInfo in store.graphQLSchema.getState()) {
        val schemaNode = DefaultMutableTreeNode(schemaInfo)
        for (member in schemaInfo.members) {
            val memberNode = DefaultMutableTreeNode(member)
            schemaNode.add(memberNode)
        }
        rootNode.add(schemaNode)
    }

    val backgroundColor = store.theme.getState().globalScheme.defaultBackground

    val tree = JXTree(DefaultTreeModel(rootNode)).apply {
        cellRenderer = SchemaTreeCellRenderer()
        isRootVisible = false
        background = backgroundColor
    }

    for (i in 0 until tree.rowCount) {
        tree.expandRow(i)
    }

    val scrollPane = JBScrollPane(tree).apply {
        background = backgroundColor
    }
    store.graphQLSchema.addListener {
        val backgroundColor = store.theme.getState().globalScheme.defaultBackground
        tree.background = backgroundColor
        tree.background = backgroundColor
    }
    return scrollPane
}