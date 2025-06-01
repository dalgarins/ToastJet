package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.model.enums.RawType
import java.util.Date

data class RequestData(
    var url: String = "",
    var name: String = "",
    var method: HttpMethod = HttpMethod.GET,
    var bodyTypeState: BodyType = BodyType.None,
    var rawTypeState: RawType = RawType.JSON,
    var expandState: Boolean = false,
    var headers: MutableList<KeyValueChecked> = mutableListOf(),
    var params: MutableList<KeyValueChecked> = mutableListOf(),
    var path: MutableList<KeyValue> = mutableListOf(),
    val formData: MutableList<FormData> = mutableListOf(),
    val urlEncoded: MutableList<KeyValueChecked> = mutableListOf(),
    var binary: String = "",
    var xml: String = "",
    var json: String = "",
    var invokedAt: Date = Date(),
    var text: String = "",
    var graphQl: GraphQLData = GraphQLData(),
    var js: String = "",
    var html: String = "",
    var cookie: MutableList<CookieData> = mutableListOf(),
    var id: String = ""
)

data class GraphQLData(
    var query: String = "",
    var variable: String = "",
)

