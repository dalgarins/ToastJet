package com.ronnie.toastjet.swing.store

import com.google.gson.Gson
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.vfs.readText
import com.intellij.openapi.vfs.writeText
import com.ronnie.toastjet.model.data.RequestData
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class RequestStore(
    val appStore: AppStore,
) {
    private val gson = Gson()
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var saveTask: Runnable? = null
    val state = StateHolder(RequestData())


    private fun scheduleSave() {
        saveTask?.let { executor.schedule({}, 0, TimeUnit.MILLISECONDS) }
        saveTask = Runnable {
            saveRequest()
        }
        saveTask?.let { executor.schedule(it, 500, TimeUnit.MILLISECONDS) }
    }

    private fun saveRequest() {
        val json = gson.toJson(state.getState())

        ApplicationManager.getApplication().invokeLater {
            runWriteAction {
                try {
                    appStore.file.writeText(json)
                } catch (e: Exception) {
                    println("Write failed: ${e.message}")
                }
            }
        }
    }

    init {
        val requestText = appStore.file.readText()
        if (appStore.file.name != "config.toast") {
            try {
                if (requestText.isNotBlank()) {
                    val rs = gson.fromJson(requestText, RequestData::class.java)
                    state.setState { rs }
                }
            } catch (err: Exception) {
                state.setState { RequestData() }
            }

        }
        state.addListener { scheduleSave() }
    }
}