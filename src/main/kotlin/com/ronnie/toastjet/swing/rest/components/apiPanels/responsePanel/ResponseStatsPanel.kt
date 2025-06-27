package com.ronnie.toastjet.swing.rest.components.apiPanels.responsePanel

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.model.data.ResponseData
import com.ronnie.toastjet.swing.store.StateHolder
import java.awt.Font
import java.text.SimpleDateFormat
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class ResponseStatsPanel(theme: StateHolder<EditorColorsManager>,response:StateHolder<out ResponseData>) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        background = theme.globalScheme.defaultBackground
    }


    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    init {

        setTheme(theme.getState())
        theme.addListener(this::setTheme)

        val response = response.getState()
        layout = BoxLayout(this, BoxLayout.X_AXIS)
        border = JBUI.Borders.empty(10, 10, 10, 0)
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

        add(createLabel("Size :"))
        add(createLabel(formatSize(response.size.toLong()), isValue = true))

        add(createLabel("Time Taken :"))
        add(createLabel(formatTime(response.timeTaken), isValue = true))

        add(createLabel("Invoked At :"))
        add(createLabel(formatter.format(response.invokedAt), isValue = true))
    }
}

fun formatTime(ms: Long): String {
    val seconds = (ms / 1000).toInt()
    val milliseconds = ms % 1000
    return if (seconds == 0) "${milliseconds}ms" else "${seconds}s ${milliseconds}ms"
}

fun formatSize(bytes: Long): String {
    val kilo = 1024.0
    val mega = kilo * 1024.0
    val giga = mega * 1024.0

    return when {
        bytes >= giga -> "%.2f GB".format(bytes / giga)
        bytes >= mega -> "%.2f MB".format(bytes / mega)
        bytes >= kilo -> "%.2f KB".format(bytes / kilo)
        else -> "$bytes B"
    }
}