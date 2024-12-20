import {
  TestFunction as TF,
  FunctionProps,
  FunctionInfo,
  VariableInfo,
  EnvironmentInfo,
} from "../../interfaces/variableData";

export default function extractConfig(rawCode: string) {
  let funs: {
    tests: FunctionInfo[];
    generators: FunctionInfo[];
  } = {
    tests: [],
    generators: [],
  };
  let vars: VariableInfo[] = [];
  let envs: EnvironmentInfo[] = [];

  function TestFunction(fn: TF, props: FunctionProps) {
    funs.tests.push({
      name: props.name,
      example: props.example,
      description: props.description,
      params: props.params,
      fn: fn,
    });
  }

  function GeneratorFunction(fn: Function, props: FunctionProps) {
    funs.generators.push({
      name: props.name,
      example: props.example,
      description: props.description,
      params: props.params,
      fn: fn,
    });
  }
  eval(`
    ${rawCode}
  `);
  return {
    funs,
    vars,
    envs,
  };
}
