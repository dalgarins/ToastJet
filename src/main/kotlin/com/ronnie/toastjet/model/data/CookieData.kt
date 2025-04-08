package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.CookieSameSite
import java.util.Date

data class CookieData (
    var enable:Boolean,
    var key : String,
    var value: String,
    var httpOnly : Boolean,
    var domain : String,
    var path : String,
    var secure:Boolean,
    var sameSite: CookieSameSite,
    var hostOnly : Boolean,
    var pathIsDefault:Boolean,
    var creationTime : Date,
    var expiryTime: Date
)