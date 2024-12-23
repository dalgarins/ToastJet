package com.aayam.toasteditor.cache

object RequestCache {
    var apis : MutableList<String> = mutableListOf()

    fun initialize(data : MutableList<String>){
        this.apis = data
    }
}