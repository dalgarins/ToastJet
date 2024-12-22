package com.aayam.toasteditor.messageHandler.variableHandler

import com.aayam.toasteditor.utilities.extractor.extractEnvs
import com.aayam.toasteditor.utilities.fileUtility.readStringFromFile
import com.google.gson.Gson
import kotlin.io.path.Path
import kotlin.io.path.pathString

fun loadEnvironmentHandler(baseDir: String, relativePath: String): String? {
    try {
        val data = readStringFromFile(Path(baseDir).resolve(relativePath).pathString)
        val envs = extractEnvs(data)
        val gSon = Gson()
        return gSon.toJson(envs)
    } catch (err: Exception) {
        println("There was error loading the environment ")
        println(err)
        return null
    }
}

