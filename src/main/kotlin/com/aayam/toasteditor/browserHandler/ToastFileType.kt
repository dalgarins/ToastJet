package com.aayam.toasteditor.browserHandler

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class ToastFileType : FileType {

    override fun getName(): String {
        return "Toast File"
    }

    override fun getDescription(): String {
        return "Toast Editor file (.tos)"
    }

    override fun getDefaultExtension(): String {
        return "tos"
    }

    override fun getIcon(): Icon {
        return IconLoader.getIcon("/icons/icon.png", ToastFileType::class.java)
    }

    override fun isBinary(): Boolean = false
}
