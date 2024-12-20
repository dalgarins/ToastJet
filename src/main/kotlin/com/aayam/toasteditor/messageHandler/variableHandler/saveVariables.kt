package com.aayam.toasteditor.messageHandler.variableHandler

import com.aayam.toasteditor.cache.VariableCache
import com.intellij.openapi.vfs.VirtualFile

fun saveVariablesHandler   (
    file:VirtualFile,
    data:String
){
    try{
        VariableCache.initialize(data)
        if(file.isWritable){
            file.getOutputStream(null).use { o->
                o.write(data.toByteArray(Charsets.UTF_8))
            }
        }
    }catch(err : Exception){
        println("Error while writing to the file")
        println(err.message)
    }
}