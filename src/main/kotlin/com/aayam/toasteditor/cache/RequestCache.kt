package com.aayam.toasteditor.cache

object RequestCache {
    var apis : List<String> = emptyList()

    fun initialize(data : List<String>){
        this.apis = data
    }
}