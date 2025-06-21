package com.ronnie.toastjet.swing.rest.components.apiPanels

import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.SegmentedButton
import com.intellij.ui.dsl.builder.components.SegmentedButtonComponent
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.uiUtils.gbcLayout
import java.awt.*
import javax.swing.*

class RequestNamePanel(private val store: RequestStore) : JPanel(GridBagLayout()) {


    private val nameLabel = JLabel(store.nameState.getState()).apply {
        horizontalAlignment = SwingConstants.LEFT
        font = Font("Sans", Font.PLAIN, 16)
        foreground = JBColor.DARK_GRAY
        store.nameState.addListener {
            text = it
            repaint()
            revalidate()
        }
    }

    private val segmentedButtonComponent = SegmentedButtonComponent<String> {
        object : SegmentedButton.ItemPresentation {
            override var enabled: Boolean = true
            override var icon: Icon? = null
            override var text: String? = it
            override var toolTipText: String? = null
        }
    }.apply {
        items = listOf("Api Test", "Examples")
        selectedItem = "Api Test"
        minimumSize = Dimension(400, minimumSize.height)
    }


    init {
        background = JBColor.background()
        preferredSize = Dimension(0, 45)
        maximumSize = Dimension(Int.MAX_VALUE, 45)

        val gbc = GridBagConstraints().apply {
            fill = GridBagConstraints.HORIZONTAL
            insets = JBUI.insets(5)
        }

        add(Box.createHorizontalStrut(3), gbcLayout(gbc,x-0, weightX = 0.0))

        gbc.anchor = GridBagConstraints.WEST
        add(nameLabel, gbcLayout(gbc,x=1, weightX = 5.0))

        gbc.anchor = GridBagConstraints.EAST
        add(segmentedButtonComponent, gbcLayout(gbc,x=2, weightX = 0.5))
    }
}
