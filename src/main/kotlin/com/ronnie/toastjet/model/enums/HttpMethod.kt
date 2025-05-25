package com.ronnie.toastjet.model.enums

enum class HttpMethod {
    GET,
    POST,
    PUT,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS,
    CONNECT,
    TRACE,
    PURGE,
    LINK,
    UNLINK,
    PROPFIND,
    PROPPATCH,
    MKCOL,
    COPY,
    MOVE,
    LOCK,
    UNLOCK,
    REPORT,
    VIEW;

    companion object {
        val GET_TYPE_METHODS: List<HttpMethod> = listOf(
            GET, HEAD, OPTIONS, TRACE, CONNECT, PROPFIND, REPORT, VIEW
        )
        val POST_TYPE_METHODS: List<HttpMethod> = listOf(
            POST, PUT, PATCH, DELETE, PURGE, LINK, UNLINK,
            PROPPATCH, MKCOL, COPY, MOVE, LOCK, UNLOCK
        )
    }
}