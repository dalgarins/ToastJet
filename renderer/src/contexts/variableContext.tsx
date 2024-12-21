import React, {
  createContext,
  ReactElement,
  useContext,
  useEffect,
  useState,
} from "react";
import cefMessage from "../cefMessages";
import { MessageType } from "../enums/MessageType";
import extractConfig from "../handler/codeHandler/extractConfig";
import { documentSeperator } from "../constants";
import {
  EnvironmentInfo,
  FunctionInfo,
  VariableInfo,
} from "../interfaces/variableData";
import { VariableDataType } from "../enums/VariableEnum";
import { inspect } from "util-ex";

export const VariableDataContext = createContext<{
  vars: VariableInfo[];
  setVars: (a: VariableInfo[]) => void;
}>({
  vars: [],
  setVars: () => {},
});

export const EnvVariableContext = createContext<{
  paths: EnvironmentInfo[];
  setPaths: (a: EnvironmentInfo[]) => void;
}>({
  paths: [],
  setPaths: () => {},
});

export const EnvDataContext = createContext<{
  envs: {
    key: string;
    value: string;
  }[];
  setEnvs: (a: { key: string; value: string }[]) => void;
}>({
  envs: [],
  setEnvs: () => {},
});

export const RawFunctionContext = createContext<{
  rawFunction: string;
  setRawFunction: (a: string) => void;
}>({
  rawFunction: "",
  setRawFunction: () => {},
});

export const FunctionContext = createContext<{
  funs: {
    tests: FunctionInfo[];
    generators: FunctionInfo[];
  };
  setFuns: (a: { tests: FunctionInfo[]; generators: FunctionInfo[] }) => void;
}>({
  funs: {
    tests: [] as FunctionInfo[],
    generators: [] as FunctionInfo[],
  },
  setFuns: () => {},
});

export default function VariableProvider({
  children,
}: {
  children: ReactElement;
}) {
  let [varData, setVarData] = useState("");
  let [vars, setVars] = useState<VariableInfo[]>([]);
  let [paths, setPaths] = useState<EnvironmentInfo[]>([]);
  let [envs, setEnvs] = useState<
    {
      key: string;
      value: string;
    }[]
  >([]);
  let [init, setInit] = useState<boolean>(false);
  let [funs, setFuns] = useState<{
    tests: FunctionInfo[];
    generators: FunctionInfo[];
  }>({
    tests: [],
    generators: [],
  });
  let [functionData, setFunctionData] = useState("");

  useEffect(() => {
    if (init) {
      cefMessage({
        type: MessageType.SaveVariables,
        data: `vars = ${inspect(vars)}
envs= ${inspect(paths)}
${documentSeperator}
${functionData}
`,
        onSuccess: () => {},
      });
    }
  }, [vars, funs, paths]);

  useEffect(() => {
    cefMessage({
      type: MessageType.GetVariables,
      data: "",
      onSuccess: (data) => {
        if (data !== null) {
          setVarData(data);
          setFunctionData(data.split(documentSeperator).at(1)?.trim() ?? "");
          let configData = extractConfig(data);
          let lastVars = configData.vars.at(-1);
          if (lastVars?.key.trim() !== "" && lastVars?.value.trim() !== "") {
            configData.vars.push({
              key: "",
              value: "",
              enabled: true,
              type: VariableDataType.string,
            });
          }
          setVars(configData.vars);
          setPaths(configData.envs);
          configData.envs.forEach((a) => {
            cefMessage({
              type: MessageType.LoadEnvironment,
              data: "",
              onSuccess: (l) => {
                if (l === null) {
                  a.status = false;
                } else {
                  a.status = true;
                }
              },
            });
          });
          setFuns(configData.funs);
          setInit(true);
        }
      },
    });
  }, []);

  return (
    <RawFunctionContext.Provider
      value={{
        rawFunction: functionData,
        setRawFunction: setFunctionData,
      }}
    >
      <VariableDataContext.Provider value={{ vars, setVars }}>
        <EnvDataContext.Provider
          value={{
            envs,
            setEnvs,
          }}
        >
          <EnvVariableContext.Provider value={{ paths, setPaths }}>
            <FunctionContext.Provider value={{ funs, setFuns }}>
              {children}
            </FunctionContext.Provider>
          </EnvVariableContext.Provider>
        </EnvDataContext.Provider>
      </VariableDataContext.Provider>
    </RawFunctionContext.Provider>
  );
}
