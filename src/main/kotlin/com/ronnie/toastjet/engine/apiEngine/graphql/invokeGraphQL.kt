package com.ronnie.toastjet.engine.apiEngine.graphql

import com.ronnie.toastjet.model.data.GraphQLRequestData
import com.ronnie.toastjet.model.data.GraphQLResponseData

fun invokeGraphQL(req: GraphQLRequestData): GraphQLResponseData {
    try {
        return GraphQLClient.performRequest(req)
    }catch (e: Exception){
        println("The exception is $e")
        throw e
    }
}