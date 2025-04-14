package com.ronnie.toastjet.editor

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.ProcessingContext


class TJsonCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inVirtualFile(PlatformPatterns.virtualFile()),
            TjsonCompletionProvider()
        )
    }

}

class TjsonCompletionProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {
        val variableNames = listOf("username", "email", "date", "orderId")
        val prefixMatcher = resultSet.prefixMatcher.prefix
        val editor = parameters.editor
        val document = editor.document
        val offset = parameters.offset

        val textBeforeCursor = document.text.substring(0, offset)
        val lastTwoChars = textBeforeCursor.takeLast(2)

        if (lastTwoChars == "{{") {
            val adjustedPrefix = resultSet.withPrefixMatcher(prefixMatcher.removePrefix("{{").trim())
            variableNames.forEach {
                val lookupElement = LookupElementBuilder.create(it).bold()
                    .withInsertHandler { context, _ ->
                        val doc = context.document
                        val startOffset = context.startOffset
                        val endOffset = context.tailOffset
                        val text = doc.getText(TextRange(startOffset, endOffset))
                        val endsWithBraces = text.endsWith("}}")

                        val newText = when {
                            endsWithBraces -> it
                            else -> "$it}}"
                        }

                        doc.replaceString(startOffset, endOffset, newText)
                        PsiDocumentManager.getInstance(context.project).commitDocument(doc)
                    }

                adjustedPrefix.addElement(lookupElement)
            }
        } else {
            variableNames.forEach {
                val lookupElement = LookupElementBuilder.create(it).bold()
                    .withInsertHandler { context, _ ->
                        val doc = context.document
                        val startOffset = context.startOffset
                        val endOffset = context.tailOffset
                        val text = doc.getText(TextRange(startOffset, endOffset))

                        val startsWithBraces = text.startsWith("{{")
                        val endsWithBraces = text.endsWith("}}")

                        val newText = when {
                            startsWithBraces && endsWithBraces -> text // Already formatted, no change needed
                            startsWithBraces -> "$text}}" // Only add closing braces
                            endsWithBraces -> "{{$text" // Only add opening braces
                            else -> "{{${text}}}" // Add both opening and closing braces
                        }

                        doc.replaceString(startOffset, endOffset, newText)
                        PsiDocumentManager.getInstance(context.project).commitDocument(doc)
                    }
                resultSet.addElement(lookupElement)
            }
        }
    }
}

