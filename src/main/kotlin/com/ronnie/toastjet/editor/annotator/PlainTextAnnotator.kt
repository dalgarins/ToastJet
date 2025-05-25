package com.ronnie.toastjet.editor.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.ronnie.toastjet.engine.scriptExecutor.functionList
import com.ronnie.toastjet.swing.store.configStore

class PlainTextAnnotator : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        println("Were our state here $text")

        val startOffset = element.textRange.startOffset

        val doubleBraceRegex = Regex("\\{\\{(.*?)}}")
        val singleBraceRegex = Regex("(?<!\\{)\\{(.*?)}(?!})")

        val urlRegex = Regex("""(https?|ftp)://[^\s/$.?#].[^\s]*""") // URL pattern

        // Handle URLs first (since they might contain braces)
        urlRegex.findAll(text).forEach { match ->
            val matchStart = startOffset + match.range.first
            val matchEnd = startOffset + match.range.last + 1

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(matchStart, matchEnd))
                .textAttributes(DefaultLanguageHighlighterColors.STRING) // or use a custom color
                .needsUpdateOnTyping()
                .create()
        }

        // Handle {{variable}}
        configStore?.let {
            doubleBraceRegex.findAll(text).forEach { match ->
                val variableName = match.groupValues[1]
                val matchStart = startOffset + match.range.first
                val matchEnd = startOffset + match.range.last + 1

                val contentStart = matchStart + 2
                val contentEnd = matchEnd - 2
                println("Were our state here $match")
                if (!it.state.getState().vars.map { it.key }.contains(variableName) && !functionList.contains(variableName)) {
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
        }


        singleBraceRegex.findAll(text).forEach { match ->
            val matchStart = startOffset + match.range.first
            val matchEnd = startOffset + match.range.last + 1

            val contentStart = matchStart + 1
            val contentEnd = matchEnd - 1

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(matchStart, contentStart)) // {
                .textAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
                .create()

            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(contentStart, contentEnd))
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
