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
import com.intellij.codeInsight.intention.preview.IntentionPreviewUtils
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.xml.XmlAttribute
import com.ronnie.toastjet.engine.scriptExecutor.functionList
import com.ronnie.toastjet.model.NavigateTo
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.model.navigateTo
import com.ronnie.toastjet.swing.store.configStore
import com.ronnie.toastjet.utils.fileUtils.findConfigFile

class TXmlAnnotator : Annotator, DumbAware {
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val text = element.text
        val startOffset = element.textRange.startOffset
        if (!(element is XmlText ||  element is XmlAttribute)) return

        val regex = Regex("\\{\\{(.*?)}}")
        configStore?.let {
            regex.findAll(text).forEach { match ->
                val variableName = match.groupValues[1]
                val matchStart = startOffset + match.range.first
                val matchEnd = startOffset + match.range.last + 1

                val contentStart = matchStart + 2
                val contentEnd = matchEnd - 2

                // Error if variable is not in allowed list
                if (!it.state.getState().vars.map { it.key }.contains(variableName)&& !functionList.contains(variableName)) {
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
                store.appState.project.let { project ->
                    val virtualFile = findConfigFile(store.appState.file.path)?.let {
                        LocalFileSystem.getInstance().findFileByPath(it)
                    }

                    if (!ApplicationManager.getApplication().isUnitTestMode && !IntentionPreviewUtils.isIntentionPreviewActive()) {
                        WriteCommandAction.runWriteCommandAction(project) {
                            virtualFile?.let { file ->
                                file.putUserData(navigateTo, NavigateTo.Vars.name)
                                FileEditorManager.getInstance(project).openFile(file, true)
                                val varEditor = FileEditorManager.getInstance(project).selectedTextEditor
                                varEditor?.caretModel?.moveToOffset(0)

                                Notification(
                                    "File Watcher Messages",
                                    "Variable added",
                                    "Added '$invalidVariable' and opened config.toast",
                                    NotificationType.INFORMATION
                                ).notify(project)
                            } ?: run {
                                Notification(
                                    "File Watcher Messages",
                                    "File not found",
                                    "Could not find config.toast at specified path",
                                    NotificationType.WARNING
                                ).notify(project)
                            }
                        }
                    } else {
                        // Alternative handling for preview mode
                        println("Preview mode - would add '$invalidVariable'")
                    }
                }
            }
        }
        file?.let {
            DaemonCodeAnalyzer.getInstance(project).restart(it)
        }
    }
}

