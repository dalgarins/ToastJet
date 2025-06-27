package com.ronnie.toastjet.swing.graphql.graphQLRequest

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBScrollPane
import com.ronnie.toastjet.swing.store.GraphQLStore
import com.ronnie.toastjet.swing.store.StateHolder
import org.jdesktop.swingx.JXTree
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.UIManager
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeCellRenderer


class SchemaTreeCellRenderer(val theme: StateHolder<EditorColorsManager>) : JPanel(), TreeCellRenderer {

    fun setTheme(theme: EditorColorsManager){
        background = theme.globalScheme.defaultBackground
    }

    private val checkBox = JCheckBox()

    init {
        layout = BorderLayout()
        add(checkBox, BorderLayout.WEST)
        background = theme.getState().globalScheme.defaultBackground
    }

    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any?,
        sel: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        val node = value as? DefaultMutableTreeNode
        val userObject = node?.userObject

        // Set the text based on your schema object
        val text = when {
            userObject is String && userObject.startsWith("Type:") -> userObject
            userObject is String && userObject.startsWith("Query:") -> userObject
            userObject is String && userObject.startsWith("Mutation:") -> userObject
            userObject is String && userObject.startsWith("Field:") -> userObject
            userObject is String && userObject.startsWith("Arg:") -> userObject
            else -> userObject.toString()
        }

        checkBox.text = text
        checkBox.isSelected = node?.isLeaf == true // Example logic for selection
        checkBox.isOpaque = !sel // Highlight if selected

        if (sel) {
            background = UIManager.getColor("Tree.selectionBackground")
            checkBox.foreground = UIManager.getColor("Tree.selectionForeground")
        } else {
            background = theme.getState().globalScheme.defaultBackground
            checkBox.foreground = theme.getState().globalScheme.defaultForeground
        }
        setTheme(theme.getState())
        theme.addListener(this::setTheme)
        this.components.forEach {
            it.background = theme.getState().globalScheme.defaultBackground
        }
        return this
    }
}


fun createSchemaTree(store: GraphQLStore): JScrollPane {
    val rootNode = DefaultMutableTreeNode("GraphQL Schema")
    val schemaData = store.graphQLSchema.getState()
    val typesNode = DefaultMutableTreeNode("Types")
    for (type in schemaData.typeSchemas) {
        val typeNode = DefaultMutableTreeNode("Type: ${type.name}")
        for (field in type.fields) {
            val fieldText = "Field: ${field.name} → ${field.ofType ?: "?"}"
            typeNode.add(DefaultMutableTreeNode(fieldText))
        }
        typesNode.add(typeNode)
    }
    val queriesNode = DefaultMutableTreeNode("Queries")
    for (query in schemaData.queryOperations) {
        val queryNode = DefaultMutableTreeNode("Query: ${query.name} → ${query.ofType ?: "?"}")
        for (arg in query.args) {
            val argText = "Arg: ${arg.name}: ${arg.ofType ?: "?"}"
            queryNode.add(DefaultMutableTreeNode(argText))
        }
        queriesNode.add(queryNode)
    }
    val mutationsNode = DefaultMutableTreeNode("Mutations")
    for (mutation in schemaData.mutationOperations) {
        val mutationNode = DefaultMutableTreeNode("Mutation: ${mutation.name} → ${mutation.ofType ?: "?"}")
        for (arg in mutation.args) {
            val argText = "Arg: ${arg.name}: ${arg.ofType ?: "?"}"
            mutationNode.add(DefaultMutableTreeNode(argText))
        }
        mutationsNode.add(mutationNode)
    }
    rootNode.add(typesNode)
    rootNode.add(queriesNode)
    rootNode.add(mutationsNode)
    val backgroundColor = store.theme.getState().globalScheme.defaultBackground
    val tree = JXTree(DefaultTreeModel(rootNode)).apply {
        cellRenderer = SchemaTreeCellRenderer(store.theme)
        isRootVisible = false
        background = backgroundColor
    }
    for (i in 0 until tree.rowCount) {
        tree.expandRow(i)
    }
    store.theme.addListener {
        val newBackgroundColor = store.theme.getState().globalScheme.defaultBackground
        tree.background = newBackgroundColor
    }
    return JBScrollPane(tree).apply {
        background = backgroundColor
        viewport.background = backgroundColor
    }
}