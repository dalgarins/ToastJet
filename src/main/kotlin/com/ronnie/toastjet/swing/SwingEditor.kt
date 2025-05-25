package com.ronnie.toastjet.swing

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.ronnie.toastjet.swing.components.ApiContainer
import com.ronnie.toastjet.swing.components.ConfigContainer
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.store.configStore
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.UIManager

class SwingEditor(project: Project, private val virtualFile: VirtualFile) : FileEditor,
    UserDataHolderBase() {

    private val appStore = AppStore(file = file, project = project)
    private val requestStore = RequestStore(appStore)
    private val container: ApiContainer
    private val configContainer: ConfigContainer

    init {
        configStore = ConfigStore(appStore)
        container = ApiContainer(requestStore, configStore!!)
        configContainer = ConfigContainer(configStore!!)
    }


    override fun dispose() {}

    override fun getComponent(): JComponent {

        val theme = EditorColorsManager.getInstance()
        UIManager.put("Panel.background", theme.globalScheme.defaultBackground)
        UIManager.put("TextField.background", theme.globalScheme.defaultBackground)
        if (virtualFile.name == "config.toast") return configContainer
        return container
    }

    override fun getPreferredFocusedComponent(): JComponent = container

    override fun getName(): String = "Toast Editor"

    override fun setState(state: FileEditorState) {}

    override fun isModified(): Boolean = false

    override fun isValid(): Boolean = file.isValid

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {}

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {}

    override fun getFile(): VirtualFile = virtualFile

}