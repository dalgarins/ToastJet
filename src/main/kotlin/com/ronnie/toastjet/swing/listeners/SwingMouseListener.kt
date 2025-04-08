package com.ronnie.toastjet.swing.listeners

import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class SwingMouseListener(
    private val mouseClicked : ((a:MouseEvent?)->Unit )? = null,
    private val mousePressed : ((a:MouseEvent?)->Unit)? = null,
    private val mouseReleased : ((a:MouseEvent?)->Unit)? = null,
    private val mouseEnter : ((a:MouseEvent?)->Unit)? = null,
    private val mouseExit : ((a:MouseEvent?)->Unit)? = null

) : MouseListener {
    override fun mouseClicked(p0: MouseEvent?) {
        mouseClicked?.let { it(p0) }
    }

    override fun mousePressed(p0: MouseEvent?) {
        mousePressed?.let { it(p0) }
    }

    override fun mouseReleased(p0: MouseEvent?) {
        mouseReleased?.let { it(p0) }
    }

    override fun mouseEntered(p0: MouseEvent?) {
        mouseEnter?.let { it(p0) }
    }

    override fun mouseExited(p0: MouseEvent?) {
        mouseExit?.let { it(p0) }
    }
}