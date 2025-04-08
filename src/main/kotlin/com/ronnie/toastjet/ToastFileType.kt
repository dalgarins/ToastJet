package com.ronnie.toastjet

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

class ToastFileType  : FileType {
    override fun getName(): String {
        return "Toast"
    }

    override fun getDescription(): String {
        return "Toast File extension"
    }

    override fun getDefaultExtension(): String {
        return "toast"
    }

    override fun getIcon(): Icon {
        return IconLoader.getIcon("/icons/icon.png", ToastFileType::class.java)
    }

    override fun isBinary(): Boolean {
        return false
    }
}