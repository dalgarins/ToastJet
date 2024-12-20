import React, { useContext, useEffect, useRef, useState } from "react";
import { loader } from "@monaco-editor/react";
import { getThemeColors } from "../../themes/getThemeColors";
import { ConfigurationContext } from "../../contexts/configurtionContext";
import getVsCodeTheme from "../../themes/vsCodeThemes";

const JsonXmlCodeComponent = ({ setValue, type = "json", flex, id, border, value }: {
    setValue: (a: string) => void,
    value: string,
    type: "xml" | "json" | "text" | "html" | "javascript",
    flex: number,
    id: any,
    border: boolean
}) => {
    const config = useContext(ConfigurationContext)
    const theme = getThemeColors(config.isDark)
    const editorRef = useRef<any>(null);
    const [lines, setLines] = useState(8);

    useEffect(() => {
        loader.init().then((monaco) => {
            monaco.editor.defineTheme('myTransparentTheme', getVsCodeTheme(config.isDark));
            let x = document.getElementById(`editor-container-${id}`)!
            const m = monaco.editor.create(x, {
                value: value,
                language: type,
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
                console.log(typeof newValue, "Checking type of new value")
                setValue(newValue);
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

    return <div style={{
        position: "relative",
        width: "10px",
        flexGrow: flex,
        borderLeft: border ? `solid 1px ${theme.simpleBorder}` : "none"
    }}>
        <div style={{
            height: `${lines * 20 + 20}px`,
            overflow: "hidden",
            display: "flex",
            flexDirection: "row"
        }}>
            <div
                id={`editor-container-${id}`}
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

export default JsonXmlCodeComponent;
