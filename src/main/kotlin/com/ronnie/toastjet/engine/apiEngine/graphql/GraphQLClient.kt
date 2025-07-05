package com.ronnie.toastjet.engine.apiEngine.graphql

import com.ronnie.toastjet.engine.apiEngine.getOkClient
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleGraphQLData
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handleGraphQLHeaders
import com.ronnie.toastjet.engine.apiEngine.rest.utils.handlePath
import com.ronnie.toastjet.model.data.AllGraphQLSchemaInfo
import com.ronnie.toastjet.model.data.ArgumentDetails
import com.ronnie.toastjet.model.data.FieldDetails
import com.ronnie.toastjet.model.data.GraphQLRequestData
import com.ronnie.toastjet.model.data.GraphQLResponseData
import com.ronnie.toastjet.model.data.KeyValueChecked
import com.ronnie.toastjet.model.data.OperationDetails
import com.ronnie.toastjet.model.data.TypeSchema
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.swing.store.configStore
import com.ronnie.toastjet.utils.apiUtils.extractCookies
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.Duration
import java.time.LocalDateTime
import kotlin.collections.component1
import kotlin.collections.component2

object GraphQLClient {

    fun performRequest(request: GraphQLRequestData): GraphQLResponseData {
        val client = getOkClient(configStore!!, request.cookie)
        val requestUrl = handlePath(request.url, configStore?.state?.getState()?.baseDomain, request.path)
        val requestHeaders = handleGraphQLHeaders(request)
        val requestBuilder = Request.Builder()
            .url(requestUrl)
        val requestBody = handleGraphQLData(request.graphQL)
        requestBuilder.method(HttpMethod.POST.name, requestBody)
        requestHeaders.forEach { (key, value) ->
            requestBuilder.addHeader(key, value)
        }

        println("THe url invoked is $requestUrl $requestBody")
        val apiRequest = requestBuilder.build()
        try {
            val startTime = LocalDateTime.now()
            client.newCall(apiRequest).execute().use { response ->
                val endTime = LocalDateTime.now()
                val body = response.body?.string()
                val respHeaders = response.headers.toMultimap().mapValues { it.value.joinToString(", ") }
                return GraphQLResponseData(
                    invoked = true,
                    isBeingInvoked = false,
                    apiRequestData = request,
                    url = requestUrl,
                    name = "",
                    description = "",
                    requestHeaders = requestHeaders,
                    responseHeaders = respHeaders,
                    errorMessage = ArrayList(),
                    size = body?.length ?: 0,
                    status = response.code,
                    statusText = HttpStatusCode.fromValue(response.code).description,
                    data = body,
                    error = false,
                    setCookie = extractCookies(respHeaders),
                    timeTaken = Duration.between(startTime, endTime).toMillis()
                )
            }
        } catch (e: Exception) {
            println("What is the exception ")
            e.printStackTrace()
        }
        return GraphQLResponseData(
            invoked = true,
            isBeingInvoked = false,
            apiRequestData = request,
            url = requestUrl,
            name = "",
            description = "",
            requestHeaders = requestHeaders,
            responseHeaders = HashMap(),
            errorMessage = ArrayList(),
            size = 0,
            status = 400,
            statusText = "Failed",
            data = null,
            error = true,
            setCookie = ArrayList(),
            timeTaken = 0
        )
    }


    // Safe accessor helpers
    private fun JsonElement?.safeAsJsonObject(): JsonObject? =
        if (this is JsonObject) this else null

    private fun JsonElement?.safeContent(): String? =
        if (this is JsonPrimitive && this.isString) this.content else null

    fun fetchAllGraphQLSchema(
        graphqlRequest: GraphQLRequestData,
    ): AllGraphQLSchemaInfo? {
        val urlString = graphqlRequest.url
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            println("Invalid URL: Scheme missing or invalid for $urlString")
            return null
        }

        val client = getOkClient(configStore!!, graphqlRequest.cookie)

        try {
            // Step 1: Fetch all types
            val initialSchemaQuery = """
                query {
                  __schema {
                    types {
                      name
                      kind
                    }
                  }
                }
            """.trimIndent()

            val initialResult =
                executeGraphQLQuery(client, graphqlRequest.url, initialSchemaQuery, graphqlRequest.headers)
            val allTypesJson = initialResult?.jsonObject?.get("data")?.jsonObject
                ?.get("__schema")?.jsonObject
                ?.get("types")?.jsonArray ?: return null

            val actualTypes = allTypesJson.mapNotNull { it.jsonObject }
                .filter { typeObj ->
                    val kind = typeObj["kind"]?.safeContent()
                    val name = typeObj["name"]?.safeContent()
                    kind == "OBJECT" && name != null && !name.startsWith("__") && name != "Query" && name != "Mutation"
                }
                .map { it["name"]!!.safeContent()!! }

            // Step 2: Fetch fields for each actual type
            val typeSchema = actualTypes.mapNotNull { typeName ->
                val typeFieldsQuery = """
                    query {
                        __type(name: "$typeName") {
                            name
                            fields {
                                name
                                description
                                type{
                                    name
                                    kind
                                    ofType {
                                        name
                                        kind
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()

                val typeResult =
                    executeGraphQLQuery(client, graphqlRequest.url, typeFieldsQuery, graphqlRequest.headers)
                val typeObj = typeResult?.jsonObject?.get("data")?.jsonObject
                    ?.get("__type")?.jsonObject ?: return@mapNotNull null

                val fieldsJson = typeObj["fields"]?.jsonArray ?: return@mapNotNull null

                val fieldsOfEachType = fieldsJson.mapNotNull { fieldElement ->
                    val fieldObj = fieldElement.jsonObject
                    val name = fieldObj["name"]?.safeContent() ?: return null
                    val typeObj = fieldObj["type"]?.safeAsJsonObject() ?: return null

                    val ofTypeObj = when (val temp = typeObj["ofType"]) {
                        is JsonNull -> null
                        else -> temp.safeAsJsonObject()
                    }

                    val fieldKind = ofTypeObj?.get("kind")?.safeContent() ?: typeObj["kind"]?.safeContent()
                    val fieldOfType = ofTypeObj?.get("name")?.safeContent() ?: typeObj["name"]?.safeContent()
                    val nullable = ofTypeObj == null

                    FieldDetails(
                        name = name,
                        ofType = fieldOfType,
                        kind = fieldKind,
                        nullable = nullable
                    )
                }

                TypeSchema(name = typeName, fields = fieldsOfEachType)
            }

            // Step 3: Fetch operations (queries and mutations)
            val operationQuery = """
                {
                  __schema {
                    queryType {
                      name
                      fields {
                        name
                        description
                        args {
                          name
                          description
                          type {
                            name
                            kind
                            ofType {
                              name
                              kind
                            }
                          }
                        }
                        type {
                          name
                          kind
                          ofType {
                            name
                            kind
                          }
                        }
                      }
                    }
                    mutationType {
                      name
                      fields {
                        name
                        description
                        args {
                          name
                          description
                          type {
                            name
                            kind
                            ofType {
                              name
                              kind
                            }
                          }
                        }
                        type {
                          name
                          kind
                          ofType {
                            name
                            kind
                          }
                        }
                      }
                    }
                  }
                }
            """.trimIndent()

            val operationResult =
                executeGraphQLQuery(client, graphqlRequest.url, operationQuery, graphqlRequest.headers)
            val schemaObj =
                operationResult?.jsonObject?.get("data")?.jsonObject?.get("__schema")?.jsonObject ?: return null

            val queryOperationsJson =
                schemaObj["queryType"]?.jsonObject?.get("fields")?.jsonArray ?: JsonArray(emptyList())
            val mutationOperationsJson =
                schemaObj["mutationType"]?.jsonObject?.get("fields")?.jsonArray ?: JsonArray(emptyList())

            val queryOperations = queryOperationsJson.mapNotNull { parseOperationField(it.jsonObject) }
            val mutationOperations = mutationOperationsJson.mapNotNull { parseOperationField(it.jsonObject) }

            return AllGraphQLSchemaInfo(
                typeSchemas = typeSchema,
                queryOperations = queryOperations,
                mutationOperations = mutationOperations
            )
        } catch (e: Exception) {
            println("Error fetching GraphQL schema: ${e.message}")
            throw e
        }
    }

    private fun executeGraphQLQuery(
        client: OkHttpClient,
        url: String,
        query: String,
        headers: List<KeyValueChecked>
    ): JsonElement? {
        val requestBuilder = Request.Builder().url(url)

        headers.forEach {
            if (it.isChecked) {
                requestBuilder.addHeader(it.key, it.value)
            }
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = buildJsonObject {
            put("query", query)
        }.toString().toRequestBody(mediaType)

        val request = requestBuilder.post(requestBody).build()

        return client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP error ${response.code}: ${response.message}")
            Json.parseToJsonElement(response.body!!.string())
        }
    }

    // Helper function to parse common field structure for operations
    private fun parseOperationField(fieldObj: JsonObject): OperationDetails? {
        val name = fieldObj["name"]?.safeContent() ?: return null
        val typeObj = fieldObj["type"]?.safeAsJsonObject() ?: return null

        val ofTypeObj = when (val temp = typeObj["ofType"]) {
            is JsonNull -> null
            else -> temp.safeAsJsonObject()
        }

        val operationKind = ofTypeObj?.get("kind")?.safeContent() ?: typeObj["kind"]?.safeContent()
        val operationOfType = ofTypeObj?.get("name")?.safeContent() ?: typeObj["name"]?.safeContent()
        val operationNullable = ofTypeObj == null

        val argsJson = fieldObj["args"]?.jsonArray ?: JsonArray(emptyList())
        val fieldsJson = fieldObj["fields"]?.jsonArray ?: JsonArray(emptyList())


        val args = argsJson.mapNotNull { argElement ->
            val argObj = argElement.jsonObject
            val argName = argObj["name"]?.safeContent() ?: return@mapNotNull null

            val argTypeObj = argObj["type"]?.safeAsJsonObject() ?: return@mapNotNull null

            val argOfTypeObj = when (val temp = argTypeObj["ofType"]) {
                is JsonNull -> null
                else -> temp.safeAsJsonObject()
            }

            val argKind = argOfTypeObj?.get("kind")?.safeContent() ?: argTypeObj["kind"]?.safeContent()
            val argOfType = argOfTypeObj?.get("name")?.safeContent() ?: argTypeObj["name"]?.safeContent()
            val argNullable = argOfTypeObj == null

            ArgumentDetails(
                name = argName,
                kind = argKind,
                ofType = argOfType,
                nullable = argNullable
            )
        }

        val fields = fieldsJson.mapNotNull { argElement ->
            val argObj = argElement.jsonObject
            val argName = argObj["name"]?.safeContent() ?: return@mapNotNull null

            val argTypeObj = argObj["type"]?.safeAsJsonObject() ?: return@mapNotNull null

            val argOfTypeObj = when (val temp = argTypeObj["ofType"]) {
                is JsonNull -> null
                else -> temp.safeAsJsonObject()
            }

            val argKind = argOfTypeObj?.get("kind")?.safeContent() ?: argTypeObj["kind"]?.safeContent()
            val argOfType = argOfTypeObj?.get("name")?.safeContent() ?: argTypeObj["name"]?.safeContent()
            val argNullable = argOfTypeObj == null

            FieldDetails(
                name = argName,
                kind = argKind,
                ofType = argOfType,
                nullable = argNullable
            )
        }

        return OperationDetails(
            name = name,
            kind = operationKind,
            ofType = operationOfType,
            nullable = operationNullable,
            args = args,
            fields = fields
        )
    }
}