package com.ronnie.toastjet.editor.annotator

import com.intellij.lang.annotation.*
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.xml.XmlText
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.openapi.project.Project
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.HighPriorityAction
import com.intellij.psi.xml.XmlAttribute

val allowedVariables = mutableSetOf("name", "docs.owner", "done_now")

class TXmlAnnotator : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        println("The element is $element ${element.text}")
        val text = element.text
        val startOffset = element.textRange.startOffset
        if (!(element is XmlText ||  element is XmlAttribute)) return

        val regex = Regex("\\{\\{(.*?)}}")
        regex.findAll(text).forEach { match ->
            val variableName = match.groupValues[1]
            val matchStart = startOffset + match.range.first
            val matchEnd = startOffset + match.range.last + 1

            val contentStart = matchStart + 2 // After {{
            val contentEnd = matchEnd - 2     // Before }}

            // Error if variable is not in allowed list
            if (!allowedVariables.contains(variableName)) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Invalid variable reference: '$variableName'")
                    .range(TextRange(matchStart, matchEnd))
                    .withFix(AddValidVariableIntention(variableName))
                    .create()
            }

            // Highlight {{
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(matchStart, contentStart))
                .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD)
                .create()

            // Highlight variable name
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(contentStart, contentEnd))
                .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD)
                .needsUpdateOnTyping()
                .create()

            // Highlight }}
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(TextRange(contentEnd, matchEnd))
                .textAttributes(DefaultLanguageHighlighterColors.STATIC_FIELD)
                .create()
        }
    }
}

class AddValidVariableIntention(private val invalidVariable: String) : IntentionAction, HighPriorityAction {
    override fun getText(): String = "Add '$invalidVariable' as a valid variable"
    override fun getFamilyName(): String = "ADD_VALID_VARIABLE"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile?): Boolean = true
    override fun startInWriteAction(): Boolean = true

    override fun invoke(project: Project, editor: Editor?, file: PsiFile?) {
        if (!allowedVariables.contains(invalidVariable)) {
            allowedVariables.add(invalidVariable)
        }
        file?.let {
            DaemonCodeAnalyzer.getInstance(project).restart(it)
        }
    }
}
