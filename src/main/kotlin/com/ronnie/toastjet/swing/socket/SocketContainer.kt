package com.ronnie.toastjet.swing.socket

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.util.preferredHeight
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.SocketStore
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JPanel

class SocketContainer(val store: SocketStore, val configStore: ConfigStore) : JPanel() {

    fun setTheme(theme: EditorColorsManager = store.theme.getState()) {
        background = theme.globalScheme.defaultBackground
        leftPanel.background = theme.globalScheme.defaultBackground
        rightPanel.background = theme.globalScheme.defaultBackground
    }


    val leftPanel = JBScrollPane(SocketReqPanel(store, configStore)).apply {
        preferredSize = Dimension(200, preferredHeight)
        minimumSize = preferredSize
    }

    private val rightPanel = JBScrollPane(SocketResPanel(store,configStore)).apply {
        preferredSize = Dimension(200, preferredHeight)
        minimumSize = preferredSize
    }

    private val splitter = OnePixelSplitter(false, 0.2f).apply {
        dividerWidth = 1
        divider.background = JBColor.GRAY
        border = JBUI.Borders.empty()
    }

    init {
        layout = BorderLayout()
        border = JBUI.Borders.empty()
        splitter.firstComponent = leftPanel
        splitter.secondComponent = rightPanel
        add(splitter, BorderLayout.CENTER)
        setTheme()
        store.theme.addListener(this::setTheme)
    }
}