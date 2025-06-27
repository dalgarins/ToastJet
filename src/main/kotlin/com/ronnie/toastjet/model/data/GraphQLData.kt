package com.ronnie.toastjet.model.data

import com.ronnie.toastjet.model.enums.HttpMethod
import java.util.Date

data class GraphQLField(
    val name: String,
    val description: String?,
    val type: GraphQLFieldType
)

data class GraphQLFieldType(
    val name: String?,
    val kind: String?,
    val ofType: GraphQLFieldType? // For list/non-null types
) {
    val actualName: String
        get() = ofType?.name ?: name ?: kind ?: "Unknown"
    val actualKind: String
        get() = ofType?.kind ?: kind ?: "UNKNOWN"
}

data class GraphQLArgument(
    val name: String,
    val description: String?,
    val type: GraphQLFieldType
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
    val args: List<ArgumentDetails>
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