package com.aayam.toasteditor.utilities.fileUtility

import java.io.File
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.isDirectory

fun findTosResponse(path: String): String {
    val cf = findConfigTos(path)
    val responseDirectory = if (cf == null) {
        Paths.get(File(path).parent, "tos.response").toString()
    } else {
        Paths.get(File(cf).parent, "tos.response").toString()
    }
    return try {
        if (Path(responseDirectory).isDirectory()) {
            responseDirectory
        } else {
            File(responseDirectory).mkdirs()
            println("We are making dirs $responseDirectory")
            responseDirectory
        }
    } catch (err: Exception) {
        println("There is an exception finding the tos.response ${err.message}")
        responseDirectory
    }
}