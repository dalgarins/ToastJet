package com.ronnie.toastjet.swing.store

import com.google.gson.*
import com.ronnie.toastjet.model.data.ConfigData
import com.ronnie.toastjet.utils.fileUtils.findConfigFile
import com.ronnie.toastjet.utils.fileUtils.updateFile
import java.io.File

class ConfigStore(var appState: AppStore) {
    private val gson = Gson()
    var state : StateHolder<ConfigData>

    init {
        val configFile = findConfigFile(appState.file.path)
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
        state.addListener { saveConfigFile() }
    }

    private fun saveConfigFile() {
        val configData = gson.toJson(state.getState())
        updateFile(configData, appState, findConfigFile(appState.file.path))
    }
}

var configStore : ConfigStore? = null