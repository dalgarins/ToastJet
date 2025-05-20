package com.ronnie.toastjet.engine

import org.graalvm.polyglot.Context
import java.nio.file.Paths

object ScriptExecutor {
    val gralContext = Context.newBuilder("js")
        .allowAllAccess(true)
        .option("js.commonjs-require", "true")
        .option(
            "js.commonjs-require-cwd",
            Paths.get("src/main/resources").toAbsolutePath().toString()
        )
        .build()

    fun executeJsCode(data: String) {
        println("The data is $data")
//        gralContext.eval("js", data)
    }
}