package com.ronnie.toastjet.swing.components.apiPanels

import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel
import com.intellij.ui.OnePixelSplitter
import com.ronnie.toastjet.swing.store.ConfigStore
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

class ReqResComponent(store: RequestStore,configStore: ConfigStore) : JPanel() {
    private val topScrollPane = JBScrollPane(RequestComponent(store,configStore)).apply {
        preferredSize = Dimension(600, this.preferredHeight)
    }

    private val bottomScrollPane = JBScrollPane(ResponseComponent(store))
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