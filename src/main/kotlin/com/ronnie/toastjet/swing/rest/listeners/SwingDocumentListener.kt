package com.ronnie.toastjet.swing.rest.listeners

import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.editor.event.DocumentListener

class SwingDocumentListener(
    private val onChange:(a:String)->Unit
) : DocumentListener {

    override fun documentChanged(event: DocumentEvent) {
        onChange(event.document.text)
        super.documentChanged(event)
    }

}