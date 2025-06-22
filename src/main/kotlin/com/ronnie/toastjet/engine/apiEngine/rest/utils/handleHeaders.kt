package com.ronnie.toastjet.engine.apiEngine.rest.utils

import com.ronnie.toastjet.model.data.GraphQLRequestData
import com.ronnie.toastjet.model.data.RequestData
import com.ronnie.toastjet.model.enums.BodyType
import com.ronnie.toastjet.model.enums.HttpMethod
import com.ronnie.toastjet.model.enums.RawType

fun handleRestHeaders(apiRequestData: RequestData): Map<String, String> {
    val headers = apiRequestData.headers
    val validHeaders = HashMap<String, String>()
    for (x in headers) {
        if (x.isChecked && x.key.trim() != "" && x.value.trim() != "") {
            validHeaders[x.key] = x.value
        }
    }
    if (apiRequestData.method in HttpMethod.POST_TYPE_METHODS && (validHeaders["Content-Type"] == null || validHeaders["Content-Type"]?.isEmpty() == true)) {
        if (apiRequestData.bodyTypeState == BodyType.RAW) {
            validHeaders["Content-Type"] = when (apiRequestData.rawTypeState) {
                RawType.JSON -> "application/json"
                RawType.JS -> "application/javascript"
                RawType.XML -> "application/xml"
                RawType.HTML -> "text/html"
                RawType.TEXT -> "text/plain"
                RawType.GraphQL -> "application/graphql"
            }
        }
    }
    return validHeaders
}

fun handleGraphQLHeaders(graphQLData: GraphQLRequestData): Map<String, String> {
    val headers = graphQLData.headers
    val validHeaders = HashMap<String, String>()
    for (x in headers) {
        if (x.isChecked && x.key.trim() != "" && x.value.trim() != "") {
            validHeaders[x.key] = x.value
        }
    }
    if (graphQLData.method in HttpMethod.POST_TYPE_METHODS && (validHeaders["Content-Type"] == null || validHeaders["Content-Type"]?.isEmpty() == true)) {
        validHeaders["Content-Type"] = "application/json"
    }
    return validHeaders
}
