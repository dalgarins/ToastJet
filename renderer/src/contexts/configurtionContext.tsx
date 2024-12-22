import React, {
  createContext,
  ReactElement,
  useEffect,
  useState,
} from "react";
import { Configuration } from "../interfaces/configuration";
import ConfigWidget from "../widget/ConfigWidget";
import cefMessage from "../cefMessages";
import { MessageType } from "../enums/MessageType";
import VariableProvider from "./variableContext";
import ApiWidget from "../widget/ApiWidget";

const defaultConfiguration: Configuration = {
  fontSize: 14,
  isConfig: false,
  isDark: true,
};

export const ConfigurationContext =
  createContext<Configuration>(defaultConfiguration);

export function ConfigurationProvider() {
  let [config, setConfig] = useState<Configuration>(defaultConfiguration);

  let [rawData, setRawData] = useState<string[]>([]);

  useEffect(() => {
    cefMessage({
      type: MessageType.Initialize,
      data: "",
      onSuccess: (x) => {
        if (x !== null) {
          try {
            let data = JSON.parse(x);
            setConfig(data);
            cefMessage({
              type: MessageType.GetRawRequest,
              data: "",
              onSuccess: (a) => {
                if (a !== null) {
                  try {
                    let apis = JSON.parse(a);
                    setRawData(apis);
                  } catch (err) {
                    console.log("Error parsing the api request");
                  }
                }
              },
            });
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
            <ApiWidget
              rawData={rawData}
              addAtIndex={() => {}}
              onDelete={() => {}}
            />
          )}
        </ConfigurationContext.Provider>
      </VariableProvider>
    </div>
  );
}
