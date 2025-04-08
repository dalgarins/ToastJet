package com.ronnie.toastjet.model.data

data class RequestData(
    var url: String = "",
    var name: String = "",
    var bodyTypeState: Int = 0,
    var expandState:Boolean = false,
    var headers : MutableList<KeyValueChecked> = mutableListOf(),
    var params : MutableList<KeyValueChecked> = mutableListOf(),
    val path : MutableList<KeyValue> = mutableListOf(),
)

