package com.ronnie.toastjet.editor.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

class PlainTextAnnotator  : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        val startOffset = element.textRange.startOffset

        val doubleBraceRegex = Regex("\\{\\{(.*?)}}")
        val singleBraceRegex = Regex("(?<!\\{)\\{(.*?)}(?!})") // Avoid matching {{...}} or }}...

        // Handle {{variable}}
        doubleBraceRegex.findAll(text).forEach { match ->
            val variableName = match.groupValues[1]
            val matchStart = startOffset + match.range.first
            val matchEnd = startOffset + match.range.last + 1

            val contentStart = matchStart + 2
            val contentEnd = matchEnd - 2

            if (!allowedVariables.contains(variableName)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Invalid variable reference: '$variableName'")
                    .range(TextRange(matchStart, matchEnd))
                    .withFix(AddValidVariableIntention(variableName))
                    .create()
            }

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(matchStart, contentStart))
                .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD)
                .create()

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(contentStart, contentEnd))
                .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD)
                .needsUpdateOnTyping()
                .create()

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(contentEnd, matchEnd))
                .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD)
                .create()
        }

        singleBraceRegex.findAll(text).forEach { match ->
            val variableName = match.groupValues[1]
            val matchStart = startOffset + match.range.first
            val matchEnd = startOffset + match.range.last + 1

            val contentStart = matchStart + 1
            val contentEnd = matchEnd - 1

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(matchStart, contentStart)) // {
                .textAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
                .create()

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(contentStart, contentEnd)) // variable name
                .textAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
                .needsUpdateOnTyping()
                .create()

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(contentEnd, matchEnd)) // }
                .textAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
                .create()
        }
    }

}
