package com.aayam.toasteditor

import com.aayam.toasteditor.browserHandler.ToastFileType
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.project.Project

class TosWebViewEditorProvider : FileEditorProvider, DumbAware {


    override fun accept(project: Project, file: VirtualFile): Boolean {
        val fileType = ToastFileType()
        return file.fileType.name == fileType.name
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor =
         TosWebViewEditor(project,file)

    override fun getEditorTypeId(): String {
        return "TosWebViewEditor"
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}
