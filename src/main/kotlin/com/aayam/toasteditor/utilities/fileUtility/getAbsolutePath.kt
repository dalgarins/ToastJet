package com.aayam.toasteditor.utilities.fileUtility

import kotlin.io.path.Path

fun getAbsolutePath(file:String,relativePath:String): String{
    if(Path(relativePath).isAbsolute){
        return relativePath
    }else{
        val path = Path(file).resolve(relativePath).toAbsolutePath().normalize().toString()
        println("The absolute path we found was $path")
        return path
    }
}

