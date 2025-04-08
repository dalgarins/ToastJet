package com.ronnie.toastjet.swing.store

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

data class AppStore(
    val project: Project,
    val file: VirtualFile,
)

