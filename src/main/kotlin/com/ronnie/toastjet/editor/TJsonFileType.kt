package com.ronnie.toastjet.editor

import com.intellij.json.JsonFileType


class TJsonFileType : JsonFileType(TJsonLanguage.INSTANCE) {

    companion object {
        val INSTANCE = TJsonFileType()
    }

    override fun getName(): String {
        return "TJSON"
    }

    override fun getDescription(): String {
        return "TJSON"
    }

    override fun getDefaultExtension(): String {
        return "tjson"
    }
}