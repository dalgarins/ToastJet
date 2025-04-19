package com.ronnie.toastjet.editor

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.openapi.util.Key

class TJsonTypeHandler : TypedHandlerDelegate() {

    override fun checkAutoPopup(
        charTyped: Char,
        project: Project,
        editor: Editor,
        file: PsiFile
    ): Result {

        val previousChar = editor.getUserData(PREVIOUS_CHAR_KEY)

        // Update current character for next use
        editor.putUserData(PREVIOUS_CHAR_KEY, charTyped)

        if (charTyped == '{' && previousChar == '}') {
            AutoPopupController.getInstance(project).scheduleAutoPopup(editor)
            return Result.STOP
        }

        return Result.CONTINUE
    }
}

private val PREVIOUS_CHAR_KEY = Key.create<Char>("TJsonTypeHandler.PREVIOUS_CHAR")