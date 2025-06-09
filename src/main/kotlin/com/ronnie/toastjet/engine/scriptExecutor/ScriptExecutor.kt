package com.ronnie.toastjet.engine.scriptExecutor

import com.google.gson.Gson
import com.ronnie.toastjet.model.data.ReqResConfigData
import com.ronnie.toastjet.model.data.RequestData
import org.graalvm.polyglot.Context

object ScriptExecutor {
    private val toastApiDir = PackageInstaller.setupToastApiWorkspace()
    private val gson = Gson()

    fun executePrescriptCode(data: String): RequestData {
        try {
            val graalContext: Context = Context.newBuilder("js")
                .allowAllAccess(true)
                .option("js.commonjs-require", "true")
                .option(
                    "js.commonjs-require-cwd",
                    toastApiDir.absolutePath
                )
                .build()
            val generatingFunctionModule = ResourceReader.readResourceFile("Scripts/generatingFunction.js")
            val toastModule = ResourceReader.readResourceFile("Scripts/Toast.js")
            graalContext.eval(
                "js", """
            $toastModule
            $generatingFunctionModule
            let requestData
        """.trimIndent()
            )
            println("This part is working fine")
            val result = graalContext.eval(
                "js", """
            requestData = JSON.parse(JSON.stringify($data));
            eval(requestData.config.functions);
            console.log("All fine")
            requestData.api = Toast.replaceVariable(
              requestData.config.vars,
              requestData.api,
            );
            console.log("All fine 2")
            requestData.api = Toast.replaceCustomFunctions(requestData.api)
            requestData.api = Toast.replaceFunctions(requestData.api)
            console.log("All fine 3")
            this.result = JSON.stringify(requestData.api);
        """.trimIndent()
            )
            println("We are failing here")
            val request = gson.fromJson(result.toString(), RequestData::class.java)
            return request
        } catch (err: Exception) {
            println("The error is $err")
            throw err
        }
    }

    fun executePostscriptCode(data: String): ReqResConfigData {
        val graalContext: Context = Context.newBuilder("js")
            .allowAllAccess(true)
            .option("js.commonjs-require", "true")
            .option(
                "js.commonjs-require-cwd",
                toastApiDir.absolutePath
            )
            .build()
        val testFunctionModule = ResourceReader.readResourceFile("Scripts/testFunctions.js")
        val generatingFunctionModule = ResourceReader.readResourceFile("Scripts/generatingFunction.js")
        val toastModule = ResourceReader.readResourceFile("Scripts/Toast.js")
        println("Post script reaches here 0")
        graalContext.eval(
            "js", """
            $toastModule
            console.log("All fine 200")
            $generatingFunctionModule
            console.log("All fine 201")
            $testFunctionModule
            console.log("All fine 202")
            let reqResData
            let request
            let response
        """.trimIndent()
        )
        println("Post script reaches here 1")
        val result = graalContext.eval(
            "js", """
            reqResData = JSON.parse(JSON.stringify($data));
            request = reqResData.req
            response = reqResData.res
            console.log("the tes is ",reqResData.req.test)
            eval(request.test);
            console.log(JSON.stringify(reqResData.res.tests))
            this.result = JSON.stringify(reqResData);
        """.trimIndent()
        )
        println("Post script reaches here 2")
        val reqRes = gson.fromJson(result.toString(), ReqResConfigData::class.java)
        return reqRes
    }
}