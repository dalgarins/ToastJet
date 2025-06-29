package com.ronnie.toastjet.swing.graphql.graphQLRequest

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.components.JBScrollPane
import com.ronnie.toastjet.model.data.AllGraphQLSchemaInfo
import com.ronnie.toastjet.model.data.TypeSchema
import com.ronnie.toastjet.swing.store.GraphQLStore
import com.ronnie.toastjet.swing.store.StateHolder
import org.jdesktop.swingx.JXTree
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreeCellEditor
import javax.swing.tree.TreeCellRenderer

data class TreeData(
    val text: String,
    val hasCheckBox: Boolean,
    val selected: Boolean,
    val onCheck: (a: Boolean) -> Unit
)

class SchemaTreeCellRenderer(val theme: StateHolder<EditorColorsManager>) : Disposable, JPanel(), TreeCellRenderer {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        this.components.forEach {
            it.background = theme.globalScheme.defaultBackground
        }
    }

    private val checkBox = JCheckBox()
    private val label = JLabel()
    private var actualLabel: String? = null

    init {
        layout = BorderLayout()
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
        removeAll()
        val node = value as? DefaultMutableTreeNode
        val userObject = node?.userObject
        if (userObject is Boolean) {
            checkBox.isSelected = userObject
            add(checkBox, BorderLayout.WEST)
        } else if (userObject is TreeData) {
            actualLabel = userObject.text
            if (userObject.hasCheckBox) {
                checkBox.text = userObject.text
                checkBox.isSelected = userObject.selected
                checkBox.addActionListener {

                }
                checkBox.addChangeListener { userObject.onCheck(checkBox.isSelected) }
                add(checkBox, BorderLayout.WEST)
            } else {
                label.text = userObject.text
                add(label, BorderLayout.WEST)
            }
        } else {
            if (userObject is String) actualLabel = userObject
            label.text = when {
                userObject is String && userObject.startsWith("Type:") -> userObject
                userObject is String && userObject.startsWith("Query:") -> userObject
                userObject is String && userObject.startsWith("Mutation:") -> userObject
                userObject is String && userObject.startsWith("Field:") -> userObject
                userObject is String && userObject.startsWith("Arg:") -> userObject
                else -> userObject.toString()
            }
            add(label, BorderLayout.WEST)
        }
        background = theme.getState().globalScheme.defaultBackground
        checkBox.foreground = theme.getState().globalScheme.defaultForeground
        setTheme(theme.getState())
        theme.addListener(this::setTheme)
        return this
    }

    override fun dispose() {
        theme.removeListener(this::setTheme)
    }

}

class SchemaTreeCellEditor(
    val theme: StateHolder<EditorColorsManager>
) : AbstractCellEditor(), TreeCellEditor {

    private val checkBox = JCheckBox()
    private val panel = JPanel(BorderLayout())
    private var treeDataReference: TreeData? = null

    init {
        panel.background = theme.getState().globalScheme.defaultBackground
        theme.addListener { setTheme(it) }
        panel.add(checkBox, BorderLayout.WEST)
    }

    private fun setTheme(theme: EditorColorsManager) {
        panel.background = theme.globalScheme.defaultBackground
        checkBox.background = theme.globalScheme.defaultBackground
    }

    override fun getTreeCellEditorComponent(
        tree: JTree, value: Any?, isSelected: Boolean,
        expanded: Boolean, leaf: Boolean, row: Int
    ): Component {
        val node = value as? DefaultMutableTreeNode
        val userObject = node?.userObject

        if (userObject is TreeData && userObject.hasCheckBox) {
            checkBox.text = userObject.text
            checkBox.isSelected = userObject.selected
            checkBox.addActionListener {
                userObject.onCheck(checkBox.isSelected)
            }
            treeDataReference = userObject
        } else {
            checkBox.text = userObject.toString()
        }
        return panel
    }

    override fun getCellEditorValue(): Any {
        return TreeData(
            text = checkBox.text,
            hasCheckBox = true,
            selected = checkBox.isSelected,
            onCheck = {},
        )
    }
}


fun onCheck(node: DefaultMutableTreeNode, parentType: TypeSchema, iteration: Int, schemaData: AllGraphQLSchemaInfo) {
    if (iteration == 0) return
    parentType.fields.forEach { parentType ->
        val type = schemaData.typeSchemas.firstOrNull { parentType.ofType == it.name }
        val childNode = DefaultMutableTreeNode(
            TreeData(
                text = "Field: ${parentType.name} → ${parentType.ofType ?: "?"}",
                hasCheckBox = true,
                selected = false,
                onCheck = {}
            )
        )
        node.add(childNode)
        if (type != null) onCheck(childNode, type, iteration = iteration - 1, schemaData)
    }
}

fun createSchemaTree(store: GraphQLStore): JScrollPane {
    val rootNode = DefaultMutableTreeNode()
    val schemaData = store.graphQLSchema.getState()
    val queriesNode = DefaultMutableTreeNode("Queries")
    for (query in schemaData.queryOperations) {
        val queryNode = DefaultMutableTreeNode("Query: ${query.name} → ${query.ofType ?: "?"}")
        if (query.args.isNotEmpty()) {
            val argsNode = DefaultMutableTreeNode("Args")
            queryNode.add(argsNode)
            for (arg in query.args) {
                val argText = "${arg.name}: ${arg.ofType ?: "?"}"
                argsNode.add(DefaultMutableTreeNode(argText))
            }
        }
        val type = schemaData.typeSchemas.firstOrNull { it.name == query.ofType }
        val fieldsNode = DefaultMutableTreeNode("Fields")
        if (type != null) onCheck(fieldsNode, type, iteration = 8, schemaData)
        queryNode.add(fieldsNode)
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
    rootNode.add(queriesNode)
    rootNode.add(mutationsNode)
    val backgroundColor = store.theme.getState().globalScheme.defaultBackground
    val tree = JXTree(DefaultTreeModel(rootNode)).apply {
        cellRenderer = SchemaTreeCellRenderer(store.theme)
        cellEditor = SchemaTreeCellEditor(store.theme)
        isRootVisible = false
        selectionBackground = backgroundColor
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