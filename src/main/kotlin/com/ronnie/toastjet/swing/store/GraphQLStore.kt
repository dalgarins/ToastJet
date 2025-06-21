package com.ronnie.toastjet.swing.store

import com.intellij.openapi.editor.colors.EditorColorsManager

class GraphQLStore(
    val appStore: AppStore
) {
    val theme = StateHolder(EditorColorsManager.getInstance())
    val url = StateHolder("")
}