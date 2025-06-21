package com.ronnie.toastjet.swing.store

import com.intellij.openapi.editor.colors.EditorColorsManager
import com.ronnie.toastjet.model.data.CookieData
import com.ronnie.toastjet.model.data.GraphQLData
import com.ronnie.toastjet.model.data.KeyValue
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.model.data.ResponseData
import com.ronnie.toastjet.model.enums.HttpMethod

class GraphQLStore(
    val appStore: AppStore
) {
    val theme = StateHolder(EditorColorsManager.getInstance())
    val response = StateHolder(ResponseData())
    val methodState = StateHolder(HttpMethod.POST)
    val urlState = StateHolder("")
    val paramsState = StateHolder<MutableList<KeyValueChecked>>(mutableListOf())
    val headersState = StateHolder<MutableList<KeyValueChecked>>(mutableListOf())
    val pathState = StateHolder<MutableList<KeyValue>>(mutableListOf())
    val cookieState = StateHolder<MutableList<CookieData>>(mutableListOf())
    val testState = StateHolder<String>("")
    val graphQLState = StateHolder(GraphQLData())
}