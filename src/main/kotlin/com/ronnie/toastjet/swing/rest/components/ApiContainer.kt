package com.ronnie.toastjet.swing.rest.components

import java.awt.BorderLayout
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.rest.components.apiPanels.ReqResComponent
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Dimension
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class ApiContainer(private val store: RequestStore, private val configStore: ConfigStore) : JPanel(BorderLayout()) {
    private val tabbedPane = JBTabbedPane()
    private val requestTabContentPanel = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
        maximumSize = Dimension(Int.MAX_VALUE, Int.MAX_VALUE)
    }
    private val examplesPanel = JPanel(GridBagLayout()).apply {
        val label = JLabel("This is the Examples Pane").apply {
            horizontalAlignment = SwingConstants.CENTER
            verticalAlignment = SwingConstants.CENTER
            font = font.deriveFont(Font.BOLD, 24f)
        }
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            weighty = 1.0
            fill = GridBagConstraints.BOTH
        }
        add(label, gbc)
    }

    init {
        border = JBUI.Borders.empty()

        renderRequestTabContent()
        val requestScrollPane = JBScrollPane(requestTabContentPanel).apply {
            verticalScrollBarPolicy =
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED // Changed to AS_NEEDED if content overflows
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        }
        tabbedPane.addTab("Request", requestScrollPane)

        tabbedPane.addTab("Examples", examplesPanel)

        add(tabbedPane, BorderLayout.CENTER)

        store.theme.addListener {
            val theme = store.theme.getState()
            val bgColor = theme.globalScheme.defaultBackground
            val fgColor = theme.globalScheme.defaultForeground
            requestTabContentPanel.background = bgColor
            requestTabContentPanel.foreground = fgColor
            examplesPanel.background = bgColor
            examplesPanel.foreground = fgColor
            (examplesPanel.components.firstOrNull() as? JLabel)?.foreground = fgColor
            tabbedPane.background = bgColor
            tabbedPane.foreground = fgColor
        }

        val initialTheme = store.theme.getState()
        requestTabContentPanel.background = initialTheme.globalScheme.defaultBackground
        requestTabContentPanel.foreground = initialTheme.globalScheme.defaultForeground
        examplesPanel.background = initialTheme.globalScheme.defaultBackground
        examplesPanel.foreground = initialTheme.globalScheme.defaultForeground
        (examplesPanel.components.firstOrNull() as? JLabel)?.foreground = initialTheme.globalScheme.defaultForeground
    }

    private fun renderRequestTabContent() {
        requestTabContentPanel.removeAll()
        requestTabContentPanel.add(ReqResComponent(store, configStore))
        requestTabContentPanel.repaint() // These might be handled by tabbedPane/layout manager
        requestTabContentPanel.revalidate() // but good to keep for initial render
    }
}