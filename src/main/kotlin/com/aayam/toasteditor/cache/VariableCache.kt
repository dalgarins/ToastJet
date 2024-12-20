package com.aayam.toasteditor.cache

import com.aayam.toasteditor.constants.interfaces.VariableInfo

object VariableCache {
    var data : String = ""

    fun initialize(data:String){
        println("We opened the config file and the data is $data")
        this.data =  data
    }
}