package com.ronnie.toastjet.engine.scriptExecutor

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

object ResourceReader {
    fun readResourceFile(path: String?): String {
        try {
            ResourceReader::class.java.getClassLoader().getResourceAsStream(path).use { inputStream ->
                requireNotNull(inputStream) { "File not found: " + path }
                BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
                    return reader.lines().collect(Collectors.joining("\n"))
                }
            }
        } catch (e: Exception) {
            throw RuntimeException("Error reading resource file: " + path, e)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val content = readResourceFile("myfolder/myfile.txt")
        println(content)
    }
}