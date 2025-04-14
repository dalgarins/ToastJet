package com.ronnie.toastjet.swing.components.apiPanels.requestPanel.requestComponent.requestBody

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.util.ui.JBUI
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.utils.fileUtils.loadFile
import javax.swing.JPanel

import java.awt.*
import javax.swing.*

class BinaryPanel(val store: RequestStore) : JPanel() {

    private val theme = EditorColorsManager.getInstance().globalScheme


    private val uploadButton = JButton("Upload File").apply {
        background = theme.defaultBackground
        foreground = theme.defaultForeground
    }
    private val fileNameLabel = JLabel(store.state.getState().binary)
    private val deleteButton = JButton("🗑").apply {
        background = theme.defaultBackground
        foreground = theme.defaultForeground
    }


    init {
        layout = FlowLayout(FlowLayout.LEFT)
        border = JBUI.Borders.empty(20)
        background  = theme.defaultBackground
        foreground = theme.defaultForeground

        add(uploadButton)
        val fileInfoPanel = JPanel(FlowLayout(FlowLayout.CENTER))
        fileInfoPanel.background = theme.defaultBackground
        fileInfoPanel.foreground = theme.defaultForeground
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
        store.state.setState {
            it.binary = fileName
            it
        }
    }

    private fun clearFileInfo() {
        fileNameLabel.text = ""
        fileNameLabel.toolTipText = null
        fileNameLabel.parent.isVisible = false
        store.state.setState {
            it.binary = ""
            it
        }
    }
}
