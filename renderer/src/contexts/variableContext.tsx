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
  let [funs, setFuns] = useState<{
    tests: FunctionInfo[];
    generators: FunctionInfo[];
  }>({
    tests: [],
    generators: [],
  });
  let [functionData, setFunctionData] = useState("");

  useEffect(() => {
    cefMessage({
      type: MessageType.GetVariables,
      data: "",
      onSuccess: (data) => {
        if (data !== null) {
          setVarData(data);
          setFunctionData(data.split(documentSeperator).at(1) ?? "");
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
          setFuns(configData.funs);
          configData.envs.forEach((a) => {});
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
        <EnvVariableContext.Provider value={{ paths, setPaths }}>
          <FunctionContext.Provider value={{ funs, setFuns }}>
            {children}
          </FunctionContext.Provider>
        </EnvVariableContext.Provider>
      </VariableDataContext.Provider>
    </RawFunctionContext.Provider>
  );
}
