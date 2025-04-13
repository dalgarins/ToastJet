package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.RawType

data class RequestData(
    var url: String = "",
    var name: String = "",
    var bodyTypeState: BodyType = BodyType.None,
    var rawTypeState: RawType = RawType.JSON,
    var expandState: Boolean = false,
    var headers: MutableList<KeyValueChecked> = mutableListOf(),
    var params: MutableList<KeyValueChecked> = mutableListOf(),
    val path: MutableList<KeyValue> = mutableListOf(),
    val formData: MutableList<FormData> = mutableListOf(),
)

