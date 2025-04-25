package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.CookieSameSite
import java.util.Date

data class CookieData(
    var enabled: Boolean = true,
    var key: String = "",
    var value: String = "",
    var httpOnly: Boolean = false,
    var domain: String = "",
    var path: String = "/",
    var secure: Boolean = true,
    var sameSite: CookieSameSite = CookieSameSite.None,
    var hostOnly: Boolean = false,
    var pathIsDefault: Boolean = true,
    var creationTime: Date = Date(),
    var expiryTime: Date = Date(),
)