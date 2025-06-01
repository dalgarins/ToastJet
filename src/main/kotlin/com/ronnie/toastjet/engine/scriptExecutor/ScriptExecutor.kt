package com.ronnie.toastjet.engine.scriptExecutor

import com.google.gson.Gson
import com.ronnie.toastjet.model.data.RequestData
import org.graalvm.polyglot.Context

object ScriptExecutor {
    private val toastApiDir = PackageInstaller.setupToastApiWorkspace()
    private val gson = Gson()

    fun executeJsCode(data: String): RequestData {
        val graalContext: Context = Context.newBuilder("js")
            .allowAllAccess(true)
            .option("js.commonjs-require", "true")
            .option(
                "js.commonjs-require-cwd",
                toastApiDir.absolutePath
            )
            .build()
        val generatingFunctionCode = ResourceReader.readResourceFile("Scripts/generatingFunction.js")
        graalContext.eval(
            "js", """
            let funs = [];
            let Toast = {
              registerFunction: function (name, a) {
                if (typeof a === "function") {
                  funs.push({
                    name: name,
                    fun: a,
                  });
                }
              },
            
              replaceVariable: function (vars, originalData) {
                let newData = JSON.stringify(originalData);
                vars.forEach((variable) => {
                  newData = newData.replace(
                    new RegExp("{{" + variable.key + "}}", "g"),
                    variable.value,
                  );
                });
                return JSON.parse(newData);
              },
            
              replaceFunctions: function (originalData) {
                let newData = JSON.stringify(originalData);
            
                newData = newData.replace(/{{(.*?)}}/g, (match, code) => {
                  try {
                    if (code.trim().endsWith(")")) {
                      const result = eval(code); // Unsafe: only use in trusted environments
                      return result !== undefined ? result : match;
                    }
                  } catch (e) {
                    console.error(`Error evaluating function `,e);
                  }
                  return match;
                });
            
                return JSON.parse(newData);
              },
            
              replaceCustomFunctions: function (originalData, funs) {
                let newData = JSON.stringify(originalData);
            
                newData = newData.replace(/{{(.*?)}}/g, (match, code) => {
                  try {
                    const fnMatch = code.match(/^(\w+)\((.*)\)$/);
                    if (!fnMatch) return match;
                    const fnName = fnMatch[1];
                    const argString = fnMatch[2].trim();
                    const fnObj = funs.find((f) => f.name === fnName);
                    if (!fnObj) return match;
                    const args = argString ? eval("["+ argString + "]") : [];
                    const result = fnObj.fun(...args);
                    return result !== undefined ? result : match;
                  } catch (e) {
                    return match;
                  }
                });
            
                return JSON.parse(newData);
              },
            };
            let requestData
        """.trimIndent()
        )
        val result = graalContext.eval(
            "js", """
            funs = []
            requestData = JSON.parse(JSON.stringify($data));
            $generatingFunctionCode
            eval(requestData.config.functions);
            requestData.api = Toast.replaceVariable(
              requestData.config.vars,
              requestData.api,
            );
            requestData.api = Toast.replaceCustomFunctions(requestData.api)
            requestData.api = Toast.replaceFunctions(requestData.api)
            this.result = JSON.stringify(requestData.api);
        """.trimIndent()
        )
        val request = gson.fromJson(result.toString(), RequestData::class.java)
        return request
    }
}