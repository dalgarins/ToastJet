let Toast = {
  funs: [],

  setVariable: function (key, value, enable) {
    let found = false;
    for (let i = 0; i < config.vars.length; i++) {
      if (config.vars[i].key === key) {
        config.vars[i].value = value;
        if (typeof enable !== "undefined") {
          config.vars[i].checked = enable ?? true;
        }
        found = true;
        break;
      }
    }
    if (!found) {
      config.vars.push({ key: key, value: value, checked: enable ?? true });
    }
  },

  test: function (name, t) {
    let testResult = t(request, response);
    console.log("The test invoked ", name, " : ", testResult);
    reqResData.res.tests.push({
      name: name,
      result: testResult,
    });
  },

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
        console.error(`Error evaluating function `, e);
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
        const args = argString ? eval("[" + argString + "]") : [];
        const result = fnObj.fun(...args);
        return result !== undefined ? result : match;
      } catch (e) {
        return match;
      }
    });

    return JSON.parse(newData);
  },
};
