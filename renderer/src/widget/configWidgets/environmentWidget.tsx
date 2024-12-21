import React, { useContext, useEffect, useState } from "react";
import { FaCheck } from "react-icons/fa";
import { ConfigurationContext } from "../../contexts/configurtionContext";
import {
  EnvDataContext,
  EnvVariableContext,
} from "../../contexts/variableContext";
import { getThemeColors } from "../../themes/getThemeColors";
import CustomCheckbox from "../../components/Input/CheckBox";
import fileHandler from "../../handler/files/fileHandler";
import cefMessage from "../../cefMessages";
import { MessageType } from "../../enums/MessageType";

export default function EnvironmentWidget() {
  let [enable, setEnable] = useState(true);
  const config = useContext(ConfigurationContext);
  const theme = getThemeColors(config.isDark);
  const env = useContext(EnvVariableContext);

  return (
    <div
      style={{
        margin: "15px 5px",
        border: `1px solid ${theme.simpleBorder}`,
        borderRadius: "4px",
        padding: "5px 10px",
      }}
    >
      <div
        style={{
          color: theme.primaryContainer,
          fontSize: "1.25em",
          fontWeight: "bold",
          marginBottom: "10px",
        }}
      >
        Environment Variables
      </div>
      <div
        style={{ display: "flex", flexDirection: "column", padding: "5px 0" }}
      >
        <div
          style={{
            display: "flex",
            background: theme.tertiaryContainer,
            flexDirection: "row",
            borderBottom: `2px solid ${theme.simpleBorder}`,
          }}
        >
          <div style={{ width: "30px", textAlign: "center" }}>
            <FaCheck
              style={{ verticalAlign: "middle", marginLeft: "5px" }}
              color={theme.primaryContainer}
            />
          </div>
          <div
            style={{
              width: "10px",
              flexGrow: 3,
              textAlign: "center",
              fontWeight: "bold",
            }}
          >
            Environment File
          </div>
          <div style={{ width: "10px", flexGrow: 1, fontWeight: "bold" }}>
            Status
          </div>
          <div style={{ width: "30px", fontWeight: "bold" }}></div>
        </div>
        {env.paths.map((file, index) => (
          <div
            key={index}
            style={{
              display: "flex",
              flexDirection: "row",
              alignItems: "center",
              padding: "5px 0",
              borderBottom: `1px solid ${theme.simpleBorder}`,
            }}
          >
            <CustomCheckbox
              checked={file.enabled}
              onChange={(e) => {
                env.paths[index].enabled = e;
                env.setPaths([...env.paths]);
              }}
            />
            <div style={{ width: "10px", flexGrow: 3, paddingLeft: "10px" }}>
              {file.path}
            </div>
            <div
              style={{
                width: "10px",
                flexGrow: 1,
                color: file.status ? theme.successText : theme.errorText,
              }}
            >
              {file.status === undefined
                ? "Waiting"
                : file.status
                  ? "Success"
                  : "Error"}
            </div>
            <button
              onClick={() => {
                env.paths.splice(index, 1);
                env.setPaths([...env.paths]);
              }}
              style={{
                backgroundColor: "transparent",
                border: "none",
                cursor: "pointer",
                textAlign: "center",
                width: "30px",
              }}
            >
              <div style={{ margin: "0 4px", cursor: "pointer" }}>🗑️</div>
            </button>
          </div>
        ))}
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            alignItems: "center",
            paddingTop: "5px",
            borderBottom:
              env.paths.length !== 0
                ? `1px solid ${theme.simpleBorder}`
                : "none",
            height: "25px",
          }}
        >
          <CustomCheckbox checked={enable} onChange={(e) => setEnable(e)} />
          <div
            style={{
              width: "20px",
              flexGrow: 3,
              cursor: "pointer",
              color: theme.primaryContainer,
              paddingLeft: "10px",
              alignSelf: "stretch",
              display: "flex", // Use flex for centering
              justifyContent: "start", // Horizontally center content
              alignItems: "center", // Vertically center content
            }}
            onClick={async () => {
              let f = await fileHandler();
              if (f && env.paths.findIndex((x) => x.path === f) === -1) {
                cefMessage({
                  type: MessageType.LoadEnvironment,
                  data: f,
                  onSuccess: (data) => {
                    if (data !== null) {
                      env.paths.push({
                        path: f,
                        enabled: enable,
                        status: true,
                      });
                    } else {
                      env.paths.push({
                        path: f,
                        enabled: enable,
                        status: false,
                      });
                    }
                  },
                });

                env.setPaths([...env.paths]);
              }
            }}
          >
            Choose File
          </div>
          <div style={{ width: "10px", flexGrow: 1 }}>Waiting</div>
          <div style={{ width: "30px", textAlign: "center" }}></div>
        </div>
      </div>
    </div>
  );
}
