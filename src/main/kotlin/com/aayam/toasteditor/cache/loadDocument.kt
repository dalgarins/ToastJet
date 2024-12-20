package com.aayam.toasteditor.cache

import com.aayam.toasteditor.constants.enums.documentSeparator
import com.aayam.toasteditor.utilities.fileUtility.readStringFromFile
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.readText
import com.aayam.toasteditor.utilities.fileUtility.findConfigTos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Paths

var configFile : String? = null

fun loadDocument(file : VirtualFile){
    CoroutineScope(Dispatchers.Default).launch{
        configFile = findConfigTos(file.path)
        configFile?.let{ configF ->
            val configText: String = readStringFromFile(configF)
            VariableCache.initialize(configText)
            val configDir = Paths.get(configF).parent
            val responseFolderPath = configDir.resolve("tos.responses")
            if(!Files.exists(responseFolderPath)){
                Files.createDirectory(responseFolderPath)
            }
            val gitignorePath = configDir.resolve(".gitignore")
            if(Files.exists(gitignorePath)){
                val gitignoreFile = gitignorePath.toFile()
                val gitignoreContent = gitignoreFile.readText()
                if(!gitignoreContent.contains("tos.responses")){
                    gitignoreFile.appendText("\ntos.responses\n")
                }
            }else{
                Files.write(gitignorePath,listOf("tos.responses"))
            }
        }
    }

    if(file.nameWithoutExtension === "config"){
        val text = file.readText()
        RequestCache.initialize(
            text.split(documentSeparator)
        )
    }
}