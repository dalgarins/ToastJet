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
    val urlEncoded: MutableList<KeyValueChecked> = mutableListOf(),
    var binary: String = "",
    var xml: String = "",
    var json: String = "",
    var text: String = "",
    var graphQl: GraphQLData = GraphQLData(),
    var js: String = "",
    var html: String = "",
    var cookie: MutableList<CookieData> = mutableListOf(),
)

data class GraphQLData(
    var query: String = "",
    var variable: String = "",
)

