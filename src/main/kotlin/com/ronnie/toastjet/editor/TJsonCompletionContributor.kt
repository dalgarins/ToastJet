package com.ronnie.toastjet.editor

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiDocumentManager
import com.intellij.util.ProcessingContext
import com.ronnie.toastjet.swing.store.configStore
import kotlin.math.min


class TJsonCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inVirtualFile(PlatformPatterns.virtualFile()),
            TJsonCompletionProvider()
        )
    }

}

class TJsonCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        resultSet: CompletionResultSet
    ) {
        val editor = parameters.editor
        val document = editor.document
        val offset = parameters.offset
        val textBeforeCursor = document.text.substring(0, offset)

        val insideBraces = textBeforeCursor.takeLast(2) == "{{"

        if (insideBraces) {
            val adjustedPrefix = resultSet.withPrefixMatcher(resultSet.prefixMatcher.prefix.removePrefix("{{").trim())
            configStore?.let {
                val state = it.state.getState()
                state.vars.forEach { insertSuggestion(adjustedPrefix, it.key) }
            }
        } else {
            configStore?.let {
                val state = it.state.getState()
                state.vars.forEach { insertSuggestion(resultSet, it.key) }
            }
        }
    }

    private fun insertSuggestion(resultSet: CompletionResultSet, suggestion: String) {
        resultSet.addElement(
            LookupElementBuilder.create(suggestion)
                .bold()
                .withInsertHandler { insertionContext, _ ->
                    handleInsertion(insertionContext, InsertionMode.OUTSIDE_BRACES)
                }
        )
    }

    private fun handleInsertion(context: InsertionContext, mode: InsertionMode) {
        val document = context.document
        val startOffset = context.startOffset
        val endOffset = context.tailOffset
        val project = context.project

        when (mode) {
            InsertionMode.INSIDE_BRACES -> {
                val textAfter = document.text.substring(endOffset, min(endOffset + 2, document.textLength))
                if (textAfter != "}}") {
                    document.insertString(endOffset, "}}")
                }
            }

            InsertionMode.OUTSIDE_BRACES -> {
                val existingText = document.getText(TextRange(startOffset, endOffset))
                if (!existingText.startsWith("{{") || !existingText.endsWith("}}")) {
                    document.replaceString(
                        startOffset,
                        endOffset,
                        existingText
                    )
                }
            }
        }

        PsiDocumentManager.getInstance(project).commitDocument(document)
    }

    private enum class InsertionMode {
        INSIDE_BRACES,
        OUTSIDE_BRACES
    }
}
