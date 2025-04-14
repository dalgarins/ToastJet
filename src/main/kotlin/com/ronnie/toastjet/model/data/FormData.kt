package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.FormType

data class FormData(
    var enabled:Boolean=true,
    val key:String = "",
    var value:String = "",
    var type : FormType = FormType.Text
)
