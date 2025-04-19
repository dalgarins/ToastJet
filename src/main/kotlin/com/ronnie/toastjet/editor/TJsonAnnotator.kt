package com.ronnie.toastjet.editor

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.json.psi.*
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.psi.PsiElement
import com.intellij.openapi.project.DumbAware
import com.intellij.lang.annotation.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.swing.store.configStore


class TJsonAnnotator : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        when (element) {
            is JsonReferenceExpression, is JsonStringLiteral -> handleVariableValidation(element, holder)
        }
    }

    private fun handleVariableValidation(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        val startOffset = element.textRange.startOffset

        val regex = Regex("\\{\\{(.*?)}}")
        configStore?.let { store ->
            val state = store.state.getState()
            regex.findAll(text).forEach { matchResult ->
                val matchStart = startOffset + matchResult.range.first
                val matchEnd = startOffset + matchResult.range.last + 1
                val contentStart = matchStart + 2
                val contentEnd = matchEnd - 2

                val variableName = matchResult.groupValues[1]

                if (!state.vars.map { it.key }.contains(variableName)) {

                    holder.newAnnotation(HighlightSeverity.ERROR, "Invalid variable reference: '$variableName'")
                        .range(TextRange(matchStart, matchEnd))
                        .withFix(AddValidVariableIntention(variableName))
                        .create()
                }


                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(matchStart, contentStart))
                    .textAttributes(DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
                    .create()

                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(contentStart, contentEnd))
                    .textAttributes(DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
                    .needsUpdateOnTyping()
                    .create()


                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(TextRange(contentEnd, matchEnd))
                    .textAttributes(DefaultLanguageHighlighterColors.LOCAL_VARIABLE)
                    .create()
            }
        }

    }
}

class AddValidVariableIntention(private val invalidVariable: String) : IntentionAction, HighPriorityAction {
    override fun getText(): String = "Add '$invalidVariable' as a valid variable"
    override fun getFamilyName(): String = "ADD_VALID_VARIABLE"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean {
        return true
    }

    override fun startInWriteAction(): Boolean = true

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        configStore?.let { store ->
            val state = store.state.getState()
            if (!state.vars.map { it.key }.contains(invalidVariable)) {
                store.state.setState {
                    it.vars.add(
                        KeyValueChecked(
                            true,
                            invalidVariable,
                            ""
                        )
                    )
                    it
                }
            }
        }
        file?.let {
            DaemonCodeAnalyzer.getInstance(project).restart(it)
        }
    }
}
