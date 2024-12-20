import React from "react";
import { useContext, useEffect, useRef, useState } from "react";
import { loader } from "@monaco-editor/react";
import { VariableInfo } from "../../interfaces/variableData";
import { VariableDataType } from "../../enums/VariableEnum";
import { getVsCodeTheme } from "../../themes/vsCodeThemes";
import { ConfigurationContext } from "../../contexts/configurtionContext";


function getStringFromVars(vars: VariableInfo[]): string {
    let result = "";
    for (let v of vars) {
        if (v.key === "" && v.value === "") continue;

        let valueString = "";

        if (v.type === VariableDataType.boolean) {
            valueString = v.value ? "true" : "false";
        } else if (v.type === VariableDataType.string) {
            valueString = `"${v.value}"`;
        } else if (v.type === VariableDataType.number) {
            valueString = `${v.value}`;
        }

        if (v.enabled) {
            result += `\n${v.key} = ${valueString}`;
        } else {
            result += `\n//${v.key} = ${valueString}`;
        }
    }
    return result.trim();
}

function parseValue(rawValue: string): { value: any, type: VariableDataType } {
    if (rawValue === "true" || rawValue === "false") {
        return { value: rawValue === "true", type: VariableDataType.boolean };
    }
    if (!isNaN(Number(rawValue))) {
        return { value: Number(rawValue), type: VariableDataType.number };
    }
    if (rawValue.startsWith(`"`) && rawValue.endsWith(`"`)) {
        return { value: rawValue.slice(1, -1), type: VariableDataType.string };
    }
    return { value: null, type: VariableDataType.string }; // Fallback
}

function getVarsFromString(value: string): VariableInfo[] {
    const lines = value.split("\n").map(line => line.trim());
    const result: VariableInfo[] = [];

    for (const line of lines) {
        if (line.startsWith("//")) {
            // Handle disabled variables
            const match = line.substring(2).split("=");
            if (match.length === 2) {
                const key = match[0].trim();
                const rawValue = match[1].trim();
                const { value, type } = parseValue(rawValue);

                if (key && value !== null) {
                    result.push({
                        key,
                        value,
                        enabled: false,
                        type,
                    });
                }
            }
        } else {
            // Handle enabled variables
            const match = line.split("=");
            if (match.length === 2) {
                const key = match[0].trim();
                const rawValue = match[1].trim();
                const { value, type } = parseValue(rawValue);

                if (key && value !== null) {
                    result.push({
                        key,
                        value,
                        enabled: true,
                        type,
                    });
                }
            }
        }
    }

    result.push({
        key: "",
        value: "",
        enabled: true,
        type: VariableDataType.string,
    });

    return result;
}


const VariableCodeComponent = ({ vars, setVars }: {
    vars: VariableInfo[],
    setVars: (vars: VariableInfo[]) => void,
}) => {
    let [init, setInit] = useState(false)
    const config = useContext(ConfigurationContext)
    const editorRef = useRef<any>(null);
    const [lines, setLines] = useState(20);
    let [textVars, setTextVars] = useState(getStringFromVars(vars));

    useEffect(() => {
        if (init) {
            setVars(getVarsFromString(textVars));
        }
        setInit(true)
    }, [textVars])

    useEffect(() => {
        loader.init().then((monaco) => {
            monaco.editor.defineTheme('myTransparentTheme', getVsCodeTheme(config.isDark));
            let x = document.getElementById("editor-container-var")!
            const m = monaco.editor.create(x, {
                value: textVars,
                language: 'javascript',
                automaticLayout: true,
                lineHeight: 20,
                scrollBeyondLastLine: false,
                theme: "myTransparentTheme",
                minimap: {
                    enabled: false
                },
            });

            editorRef.current = m;

            m.onDidChangeModelContent(() => {
                const newValue = m.getValue();
                setTextVars(newValue);
                updateEditorHeight(m);
            });

            window.addEventListener('resize', () => {
                editorRef.current.layout();
            });

            updateEditorHeight(m);

            function scrollEventListener(event: WheelEvent) {
                event.preventDefault();
                const scrollAmountY = event.deltaY;
                const scrollAmountX = event.deltaX;
                window.scrollBy({
                    top: scrollAmountY,
                    left: scrollAmountX,
                    behavior: "smooth"
                });
            }

            x.addEventListener("wheel", scrollEventListener, {
                capture: true,
            })

            return () => {
                if (editorRef.current) {
                    editorRef.current.dispose();
                }
                window.removeEventListener('resize', () => {
                    editorRef.current.layout();
                });
                x.removeEventListener("wheel", scrollEventListener)
            };
        });
    }, []);

    const updateEditorHeight = (editor: any) => {
        const lineCount = editor.getModel()?.getLineCount() || 1;
        if (lineCount > lines) {
            setLines(lineCount)
        }
        editor.layout();
    };

    return <div style={{ width: "100%" }}>
        <div style={{
            height: `${lines * 20 + 20}px`,
            overflow: "hidden",
            display: "flex",
            flexDirection: "row"
        }}>
            <div
                id="editor-container-var"
                style={{
                    flexGrow: 1,
                    width: "10px",
                    overflow: "hidden",
                    height: '100%',
                }}
            />
        </div>
    </div>
};

export default VariableCodeComponent