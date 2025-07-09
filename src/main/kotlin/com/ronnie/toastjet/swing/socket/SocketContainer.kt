package com.ronnie.toastjet.swing.socket

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.socket.request.SocketReqPanel
import com.ronnie.toastjet.swing.socket.response.SocketResPanel
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JPanel

class SocketContainer(val store: SocketStore, val configStore: ConfigStore) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
        topScrollPane.background = theme.globalScheme.defaultBackground
        bottomScrollPane.background = theme.globalScheme.defaultBackground
    }

    private val topScrollPane = JBScrollPane(SocketReqPanel(store, configStore)).apply {
        preferredSize = Dimension(600, this.preferredHeight)
    }

    private val bottomScrollPane = JBScrollPane(SocketResPanel(store, configStore))
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
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }

    private fun updateSplitterOrientation() {
        val isHorizontal = width <= 1500

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