package com.ronnie.toastjet.editor

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.psi.PsiErrorElement

class ErrorFilter : HighlightErrorFilter() {
    override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
        println("element ${element.parent} ${element.errorDescription} ${element.containingFile}")
        return false
    }
}