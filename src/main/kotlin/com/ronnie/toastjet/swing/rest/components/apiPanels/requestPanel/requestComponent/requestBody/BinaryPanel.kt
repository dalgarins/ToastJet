package com.ronnie.toastjet.swing.rest.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.fileUtils.loadFile
import javax.swing.JPanel

import java.awt.*
import javax.swing.*

class BinaryPanel(val store: RequestStore) : JPanel() {

    fun setTheme(theme: EditorColorsManager) {
        uploadButton.background = theme.globalScheme.defaultBackground
        uploadButton.foreground = theme.globalScheme.defaultForeground
        deleteButton.foreground = theme.globalScheme.defaultForeground
        deleteButton.background = theme.globalScheme.defaultBackground
        foreground = theme.globalScheme.defaultForeground
        background = theme.globalScheme.defaultBackground
        fileInfoPanel.background = theme.globalScheme.defaultBackground
        fileInfoPanel.foreground = theme.globalScheme.defaultForeground
    }

    private val uploadButton = JButton("Upload File")

    private val fileNameLabel = JLabel(store.binaryState.getState())
    private val deleteButton = JButton("🗑")
    val fileInfoPanel = JPanel(FlowLayout(FlowLayout.CENTER))

    init {
        layout = FlowLayout(FlowLayout.LEFT)
        border = JBUI.Borders.empty(20)

        setTheme(store.theme.getState())
        store.theme.addListener(this::setTheme)

        add(uploadButton)

        fileInfoPanel.add(fileNameLabel)
        fileInfoPanel.add(deleteButton)
        fileInfoPanel.isVisible = fileNameLabel.text.trim().isNotEmpty()
        add(fileInfoPanel)
        uploadButton.addActionListener {
            loadFile { file ->
                updateFileInfo(file.path)
            }
        }
        deleteButton.addActionListener {
            clearFileInfo()
        }
    }

    private fun updateFileInfo(fileName: String) {
        fileNameLabel.text = fileName
        fileNameLabel.toolTipText = fileName
        deleteButton.isVisible = true
        fileNameLabel.isVisible = true
        fileNameLabel.parent.isVisible = true
        store.binaryState.setState(fileName)
    }

    private fun clearFileInfo() {
        fileNameLabel.text = ""
        fileNameLabel.toolTipText = null
        fileNameLabel.parent.isVisible = false
        store.binaryState.setState("")
    }
}
