package com.ronnie.toastjet.swing

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.ronnie.toastjet.ToastFileType

class SwingEditorProvider : FileEditorProvider,DumbAware {
        override fun accept(project: Project, file: VirtualFile): Boolean {
            val fileType = ToastFileType()
            return file.fileType.name == fileType.name
        }

        override fun createEditor(project: Project, file: VirtualFile): FileEditor {
            return SwingEditor(project, file)
        }

        override fun getEditorTypeId(): String {
            return "toast-editor"
        }

        override fun getPolicy(): FileEditorPolicy {
            return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR
        }

}