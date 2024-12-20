import React, {
  createContext,
  ReactElement,
  useContext,
  useEffect,
  useState,
} from "react";
import { Configuration } from "../interfaces/configuration";
import ConfigWidget from "../widget/ConfigWidget";
import cefMessage from "../cefMessages";
import { MessageType } from "../enums/MessageType";
import VariableProvider from "./variableContext";

const defaultConfiguration: Configuration = {
  fontSize: 14,
  isConfig: false,
  isDark: true,
};

export const ConfigurationContext =
  createContext<Configuration>(defaultConfiguration);

export function ConfigurationProvider() {
  let [config, setConfig] = useState<Configuration>(defaultConfiguration);

  useEffect(() => {
    cefMessage({
      type: MessageType.Initialize,
      data: "",
      onSuccess: (x) => {
        console.log("The raw data is ", x);
        if (x !== null) {
          try {
            let data = JSON.parse(x);
            setConfig(data);
          } catch (err) {
            console.log("Erorr at configuration context", err);
          }
        }
      },
    });
  }, []);

  return (
    <div
      style={{
        fontFamily: '"Poppins", "sans-serif"',
        overflowY: "auto",
      }}
    >
      <VariableProvider>
        <ConfigurationContext.Provider value={config}>
          {config.isConfig ? (
            <ConfigWidget />
          ) : (
            <div>This is a non config component</div>
          )}
        </ConfigurationContext.Provider>
      </VariableProvider>
    </div>
  );
}
