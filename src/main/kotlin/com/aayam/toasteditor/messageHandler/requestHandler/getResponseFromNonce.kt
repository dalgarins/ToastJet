package com.aayam.toasteditor.messageHandler.requestHandler

import com.aayam.toasteditor.utilities.fileUtility.findTosResponse
import com.aayam.toasteditor.utilities.fileUtility.readStringFromFile
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.exists

fun getResponseFromNonce(docPath:String,nonce:String):String? {
    val tosResponse = findTosResponse(docPath)
    val filePath = Path(tosResponse,"$nonce.json")
    if(filePath.exists()) {
     return readStringFromFile(filePath.toString())
    }
    return null
}