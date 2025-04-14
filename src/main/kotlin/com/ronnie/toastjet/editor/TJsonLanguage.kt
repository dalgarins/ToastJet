package com.ronnie.toastjet.editor

import com.intellij.json.JsonLanguage

class TJsonLanguage : JsonLanguage("TJSON","application/json" ) {
    override fun hasPermissiveStrings(): Boolean {
        return true
    }

    companion object {
        val INSTANCE = TJsonLanguage()
    }
}