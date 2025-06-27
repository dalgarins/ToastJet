package com.ronnie.toastjet.engine.scriptExecutor

import com.google.gson.Gson
import org.graalvm.polyglot.Context

object ScriptExecutor {
    val toastApiDir = PackageInstaller.setupToastApiWorkspace()
    val gson = Gson()

    inline fun <reified T> executePrescriptCode(data: String): T {
        try {
            val graalContext = Context.newBuilder("js")
                .allowAllAccess(true)
                .option("js.commonjs-require", "true")
                .option("js.commonjs-require-cwd", toastApiDir.absolutePath)
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

            val result = graalContext.eval(
                "js", """
                requestData = JSON.parse(JSON.stringify($data));
                eval(requestData.config.functions);
                requestData.api = Toast.replaceVariable(requestData.config.vars, requestData.api);
                requestData.api = Toast.replaceCustomFunctions(requestData.api);
                requestData.api = Toast.replaceFunctions(requestData.api);
                this.result = JSON.stringify(requestData.api);
            """.trimIndent()
            )

            return gson.fromJson(result.toString(), T::class.java)

        } catch (err: Exception) {
            println("The error is $err")
            throw err
        }
    }

    inline fun <reified T> executePostscriptCode(data: String): T {
        try {
            val graalContext = Context.newBuilder("js")
                .allowAllAccess(true)
                .option("js.commonjs-require", "true")
                .option("js.commonjs-require-cwd", toastApiDir.absolutePath)
                .build()

            val testFunctionModule = ResourceReader.readResourceFile("Scripts/testFunctions.js")
            val generatingFunctionModule = ResourceReader.readResourceFile("Scripts/generatingFunction.js")
            val toastModule = ResourceReader.readResourceFile("Scripts/Toast.js")

            graalContext.eval(
                "js", """
                $toastModule
                $generatingFunctionModule
                $testFunctionModule
                let reqResData
                let request
                let response
            """.trimIndent()
            )

            val result = graalContext.eval(
                "js", """
                reqResData = JSON.parse(JSON.stringify($data));
                request = reqResData.req
                response = reqResData.res
                eval(request.test);
                this.result = JSON.stringify(reqResData);
            """.trimIndent()
            )

            return gson.fromJson(result.toString(), T::class.java)

        } catch (err: Exception) {
            println("The error is $err")
            throw err
        }
    }
}