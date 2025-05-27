package com.ronnie.toastjet.swing.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import java.awt.Font
import java.text.SimpleDateFormat
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class ResponseStatsPanel(store: RequestStore) : JPanel() {
    val theme = EditorColorsManager.getInstance().globalScheme
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")


    init {
        val response = store.response.getState()
        background = theme.defaultBackground
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        border = JBUI.Borders.empty(0,10,10,0)
        alignmentX = LEFT_ALIGNMENT

        val font = Font(this.font.name, Font.PLAIN, 16)

        fun createLabel(text: String, isValue: Boolean = false): JLabel {
            return JLabel(text).apply {
                this.font = font
                if (isValue) foreground = JBColor.GREEN
                border = BorderFactory.createEmptyBorder(0, 5, 0, 5)
            }
        }

        add(createLabel("Status :"))
        add(createLabel(response.status.toString(), isValue = true))

        add(createLabel("Status Text :"))
        add(createLabel(response.statusText, isValue = true))

        add(createLabel("Time Taken :"))
        add(createLabel(formatTime(response.timeTaken), isValue = true))

        add(createLabel("Invoked At :"))
        add(createLabel(formatter.format(response.invokedAt)))
    }
}


fun formatTime(ms: Long): String {
    val seconds = (ms / 1000).toInt()
    val milliseconds = ms % 1000
    return if (seconds == 0) "${milliseconds}ms" else "${seconds}s ${milliseconds}ms"
}
