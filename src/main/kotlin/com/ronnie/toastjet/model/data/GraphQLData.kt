package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.HttpMethod
import java.util.Date

data class GraphQLFieldType(
    val name: String?,
    val kind: String?,
    val ofType: GraphQLFieldType? // For list/non-null types
)

data class TypeSchema(
    val name: String,
    val fields: List<FieldDetails>
)

data class FieldDetails(
    val name: String,
    val ofType: String?,
    val kind: String?,
    val nullable: Boolean
)

data class OperationDetails(
    val name: String,
    val kind: String?,
    val ofType: String?,
    val nullable: Boolean,
    val args: List<ArgumentDetails>,
    val fields: List<FieldDetails>
)

data class ArgumentDetails(
    val name: String,
    val kind: String?,
    val ofType: String?,
    val nullable: Boolean
)

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
    val graphQLSchema: AllGraphQLSchemaInfo = AllGraphQLSchemaInfo(),
    val graphQL: GraphQLData = GraphQLData(),
    val invokedAt: Date? = null
)

data class AllGraphQLSchemaInfo(
    val typeSchemas: List<TypeSchema> = emptyList(),
    val queryOperations: List<OperationDetails> = emptyList(),
    val mutationOperations: List<OperationDetails> = emptyList()
)

data class GraphQLResponseData(
    val apiRequestData:  GraphQLRequestData = GraphQLRequestData(),
    override var isBeingInvoked: Boolean = false,
    override var invoked: Boolean = false,
    override val invokedAt: Date = Date(),
    override val url: String = apiRequestData.url,
    override val name: String = apiRequestData.name,
    override val description: String = "",
    override val requestHeaders: Map<String, String> = HashMap(),
    override val responseHeaders: Map<String, String> = emptyMap(),
    override val error: Boolean = false,
    override val errorMessage: List<String> = emptyList(),
    override val size: Int = 0,
    override val setCookie: List<CookieData> = emptyList(),
    override val timeTaken: Long = 0L,
    override val status: Int = 200,
    override val statusText: String = "OK",
    override val data: String? = null,
    override val tests: MutableList<ResponseTest> = mutableListOf()
) : ResponseData()