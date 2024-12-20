package com.aayam.toasteditor.utilities.fileUtility

import java.io.File

fun readStringFromFile(filePath:String):String{
        return File(filePath).bufferedReader().use { it.readText() }
}