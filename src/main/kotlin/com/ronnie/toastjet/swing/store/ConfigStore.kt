package com.ronnie.toastjet.swing.store

import com.google.gson.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.vfs.writeText
import com.ronnie.toastjet.model.data.ConfigData
import com.ronnie.toastjet.utils.fileUtils.findConfigFile
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ConfigStore(var appState: AppStore) {
    private val gson = Gson()
    var state: StateHolder<ConfigData>
    var theme = StateHolder(EditorColorsManager.getInstance())
    private val configFile = findConfigFile(appState.file.path)
    private var saveTask: Runnable? = null
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    init {
        state = if (configFile != null) {
            try {
                val jsonString = File(configFile).readText()
                val configState = gson.fromJson(jsonString, ConfigData::class.java)
                StateHolder(configState)
            } catch (e: Exception) {
                println("Error loading configuration: ${e.message}")
                StateHolder(ConfigData())
            }
        } else {
            StateHolder(ConfigData())
        }
        state.addListener { scheduleSave() }
    }

    private fun scheduleSave() {
        saveTask?.let { executor.schedule({}, 0, TimeUnit.MILLISECONDS) }
        saveTask = Runnable {
            saveRequest()
        }
        saveTask?.let { executor.schedule(it, 500, TimeUnit.MILLISECONDS) }
    }


    private fun saveRequest() {
        val json = gson.toJson(state.getState())
        configFile?.let { configFile ->
            ApplicationManager.getApplication().invokeLater {
                runWriteAction {
                    try {
                        val config = File(configFile)
                        config.writeText(json)
                    } catch (e: Exception) {
                        println("Write failed: ${e.message}")
                    }
                }
            }
        }
    }
}

var configStore: ConfigStore? = null