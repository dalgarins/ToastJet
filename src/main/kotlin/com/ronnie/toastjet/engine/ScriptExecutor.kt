package com.ronnie.toastjet.engine

import org.graalvm.polyglot.Context

object ScriptExecutor {
    private val toastApiDir = PackageInstaller.setupToastApiWorkspace()

    val graalContext: Context = Context.newBuilder("js")
        .allowAllAccess(true)
        .option("js.commonjs-require", "true")
        .option(
            "js.commonjs-require-cwd",
            toastApiDir.absolutePath
        )
        .build()

    fun executeJsCode(data: String) {
        println("The data is $data")
        graalContext.eval("js", """
            let requestData = JSON.parse(JSON.stringify($data))
            eval(requestData.config.functions)
            let funs = []

            let Toast = {
              registerFunction: function (name, a) { 
                if (typeof a === 'function') { 
                funs.push({
                  name:name,
                    fun:a
                  })
                }
              },
              
              replaceVariable: function (vars, originalData) {
                let newData =  JSON.stringify(originalData)
                vars.forEach((variable) => {
                  newData = newData.replace(new RegExp("{{"+variable.key+"}}", 'g'), variable.value)
                })
                return JSON.parse(newData)
              }
            }
            requestData.api = Toast.replaceVariable(requestData.config.vars,requestData.api)
            console.log(JSON.stringify(requestData))
        """.trimIndent())
    }
}
