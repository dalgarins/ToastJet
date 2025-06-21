package com.ronnie.toastjet.swing.graphql

import com.intellij.ui.JBColor
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.GraphQLStore
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JLabel
import javax.swing.JPanel

class GraphQlReqResPanel(store: GraphQLStore, configStore: ConfigStore) : JPanel() {
    private val topScrollPane = JBScrollPane(JPanel().apply {
        add(JLabel("Request will go here"))
    }).apply {
        preferredSize = Dimension(600, this.preferredHeight)
    }

    private val bottomScrollPane = JBScrollPane(JPanel().apply {
        add(JLabel("Response will go here"))
    })
    private val splitter = OnePixelSplitter(false, 0.5f).apply {
        dividerWidth = 1
        divider.background = JBColor.GRAY
    }

    init {
        layout = BorderLayout()
        border = JBUI.Borders.empty()

        updateSplitterOrientation()
        add(splitter, BorderLayout.CENTER)

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                updateSplitterOrientation()
            }
        })
    }

    private fun updateSplitterOrientation() {
        val isHorizontal = width <= 1200

        splitter.apply {
            if (orientation != isHorizontal) {
                orientation = isHorizontal
                firstComponent = topScrollPane
                secondComponent = bottomScrollPane
                proportion = 0.5f

                border = JBUI.Borders.compound(
                    if (isHorizontal) JBUI.Borders.emptyTop(25) else JBUI.Borders.empty(),
                    JBUI.Borders.emptyLeft(20),
                    JBUI.Borders.emptyRight(20)
                )
            }
        }
    }
}