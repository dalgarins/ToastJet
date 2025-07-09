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
    private val splitter = OnePixelSplitter(true, 0.5f).apply {
        dividerWidth = 1
        divider.background = JBColor.GRAY
        firstComponent = topScrollPane
        secondComponent = bottomScrollPane
    }

    init {
        layout = BorderLayout()
        border = JBUI.Borders.empty()
        add(splitter, BorderLayout.CENTER)
        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)
    }

}