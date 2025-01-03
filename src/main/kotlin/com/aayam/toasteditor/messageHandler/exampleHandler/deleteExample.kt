package com.aayam.toasteditor.messageHandler.exampleHandler

import com.aayam.toasteditor.utilities.fileUtility.getAbsolutePath
import java.io.File

fun deleteExample(file:String,path:String){
    val absolutePath = getAbsolutePath(file = file, relativePath = path)
    val targetFile= File(absolutePath)
    if(targetFile.exists()){
        targetFile.delete()
    }else{
        println("The target file does not exist")
    }
}