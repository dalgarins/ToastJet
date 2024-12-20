package com.aayam.toasteditor.messageHandler.fileHandler

import com.google.gson.Gson
import java.io.File

fun fileDeleteHandler(currentDir:String,relativeDir:String) : String{
    val file = if (File(relativeDir).isAbsolute) {
        File(relativeDir)
    } else {
        File(currentDir, relativeDir)
    }
    val gson = Gson()
    if(file.exists()){
        file.delete()
        return gson.toJson(mapOf("error" to false))
    }else{
        return gson.toJson(mapOf("error" to true))
    }
}