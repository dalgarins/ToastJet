package com.ronnie.toastjet.utils

import java.util.UUID

fun generateRandomUuid(): String {
    val uuid = UUID.randomUUID().toString()
    return uuid
}