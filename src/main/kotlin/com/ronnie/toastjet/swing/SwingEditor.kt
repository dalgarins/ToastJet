package com.ronnie.toastjet.swing

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.ronnie.toastjet.swing.graphql.GraphQLContainer
import com.ronnie.toastjet.swing.rest.components.ApiContainer
import com.ronnie.toastjet.swing.rest.components.ConfigContainer
import com.ronnie.toastjet.swing.socket.SocketContainer
import com.ronnie.toastjet.swing.store.AppStore
import com.ronnie.toastjet.swing.store.ConfigStore
import com.ronnie.toastjet.swing.store.GraphQLStore
import com.ronnie.toastjet.swing.store.RequestStore
import com.ronnie.toastjet.swing.store.SocketStore
import com.ronnie.toastjet.swing.store.configStore
import java.beans.PropertyChangeListener
import javax.swing.JComponent
import javax.swing.SwingUtilities

class SwingEditor(private val project: Project, private val virtualFile: VirtualFile) : FileEditor,
    UserDataHolderBase(), Disposable {

    private val appStore = AppStore(file = file, project = project)
    private val requestStore = RequestStore(appStore)
    private val graphQlStore = GraphQLStore(appStore)
    private val socketStore = SocketStore(appStore)
    private val container: ApiContainer
    private val configContainer: ConfigContainer
    private val graphQLContainer: GraphQLContainer
    private val socketContainer: SocketContainer


    private fun registerThemeChangeListener() {
        val connection = project.messageBus.connect(this)
        val editorColorListener = EditorColorsListener {
            SwingUtilities.invokeLater {
                requestStore.theme.setState(EditorColorsManager.getInstance())
                configStore?.theme?.setState(EditorColorsManager.getInstance())
            }
        }
        connection.subscribe(EditorColorsManager.TOPIC, editorColorListener)
    }

    init {
        configStore = ConfigStore(appStore)
        container = ApiContainer(requestStore, configStore!!)
        configContainer = ConfigContainer(configStore!!, requestStore)
        graphQLContainer = GraphQLContainer(graphQlStore, configStore = configStore!!)
        socketContainer = SocketContainer(socketStore, configStore!!)
        registerThemeChangeListener()
    }


    override fun dispose() {
        //EditorColorsListener requires a disposable class
    }

    override fun getComponent(): JComponent {
        if (virtualFile.name == "config.toast") return configContainer
        val fileSplit = virtualFile.name.split(".")
        if (fileSplit.size > 2) {
            val secondLast = fileSplit[fileSplit.size - 2].lowercase().trim()
            if (secondLast == "graphql") {
                return graphQLContainer
            }
            if (secondLast == "socket") {
                return socketContainer
            }
        }
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