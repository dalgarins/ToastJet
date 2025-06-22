package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.HttpMethod
import java.util.Date

data class Member(val name: String, val type: String)

data class SchemaInfo(val schemaName: String, val schemaType: String, val members: List<Member>)

data class GraphQLRequestData(
    val id: String = "",
    val name: String = "",
    val method: HttpMethod = HttpMethod.POST,
    val url: String = "",
    val headers: MutableList<KeyValueChecked> = mutableListOf(),
    val params: MutableList<KeyValueChecked> = mutableListOf(),
    val path: MutableList<KeyValue> = mutableListOf(),
    val cookie: MutableList<CookieData> = mutableListOf(),
    val test: String = "",
    val graphQLSchema: MutableList<SchemaInfo> = mutableListOf(),
    val graphQL: GraphQLData = GraphQLData(),
    val invokedAt: Date? = null
)
