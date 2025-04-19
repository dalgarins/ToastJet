package com.ronnie.toastjet.editor.annotator

import com.intellij.codeInsight.completion.*
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.lookup.LookupElementBuilder

import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement

val insertHandler = InsertHandler<LookupElement> { context, item ->
    val document = context.document
    val tailOffset = context.tailOffset

    // Check if '}}' already exists after the inserted variable
    val textAfter = document.text.substring(tailOffset, minOf(tailOffset + 2, document.textLength))
    if (textAfter != "}}") {
        document.insertString(tailOffset, "}}")
        context.editor.caretModel.moveToOffset(tailOffset) // Keep caret before the trailing }}
    }
}


class TXmlCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(), // This matches every element; we'll filter inside
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    resultSet: CompletionResultSet
                ) {
                    val position = parameters.position
                    val textBeforeCursor = position.text

                    // Optional: Limit only when editing inside {{... or plain {... (or tweak further)
                    if (textBeforeCursor.contains("{{") || textBeforeCursor.contains("{")) {
                        allowedVariables.forEach { variable ->
                            resultSet.addElement(LookupElementBuilder.create(variable).withInsertHandler(insertHandler))
                        }
                    }
                }
            }
        )
    }
}
