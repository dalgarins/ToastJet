package com.ronnie.toastjet.model

import com.intellij.openapi.util.Key

val navigateTo = Key<String>("navigateTo")

enum class NavigateTo{
    Vars,
    Env,
    Function,
}