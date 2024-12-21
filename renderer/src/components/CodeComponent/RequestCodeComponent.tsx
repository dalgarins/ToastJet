import React, { useContext, useEffect, useRef, useState } from "react";
import { loader } from "@monaco-editor/react";
import { FaPlay } from 'react-icons/fa'; // Importing an icon from react-icons
import { getThemeColors } from "../../themes/getThemeColors";
import { ConfigurationContext } from "../../contexts/configurtionContext";
import { getVsCodeTheme } from "../../themes/vsCodeThemes";
import { RequestContext } from "../../contexts/requestContext";
import { helperLib } from "../../monaco/helperLib";
import requestExtractor from "../../monaco/requestExtractor";


const RequestCodeComponent = ({ index }: { index: number }) => {
    const config = useContext(ConfigurationContext)
    const theme = getThemeColors(config.isDark)
    const requestData = useContext(RequestContext)
    // const responseData = useContext(ResponseContext)
    const editorRef = useRef<any>(null);
    const [lines, setLines] = useState(20);

    useEffect(() => {
        loader.init().then((monaco) => {
            monaco.languages.typescript.javascriptDefaults.addExtraLib(helperLib);
            monaco.editor.defineTheme('myTransparentTheme', getVsCodeTheme(config.isDark));
            const m = monaco.editor.create(document.getElementById('editor-container')!, {
                value: requestData.data.rawCode,
                language: 'javascript',
                automaticLayout: true,
                lineHeight: 20,
                scrollBeyondLastLine: false,
                theme: "myTransparentTheme",
                minimap: {
                    enabled: false
                },
            });

            

            m.addAction({
                id: 'run-code',
                label: 'Run Code',
                keybindings: [monaco.KeyMod.Shift | monaco.KeyCode.Enter],
                contextMenuGroupId: 'navigation',
                contextMenuOrder: 1.5,
                run: () => {
                    let text = editorRef.current.getValue()
                    requestExtractor(text)
                }
            });

            editorRef.current = m;

            m.onDidChangeModelContent(() => {
                const newValue = m.getValue();
                requestData.data.rawCode = newValue;
                requestData.setData({ ...requestData.data });
                updateEditorHeight(m);
            });

            window.addEventListener('resize', () => {
                editorRef.current.layout();
            });

            updateEditorHeight(m);

            return () => {
                if (editorRef.current) {
                    editorRef.current.dispose();
                }
                window.removeEventListener('resize', () => {
                    editorRef.current.layout();
                });
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

    return <div style={{ position: "relative", width: "100%" }}>
        <button
            // onClick={() => requestSender({
            //     data: requestData.data,
            //     response: responseData.response,
            //     setResponse: responseData.setResponse,
            //     index: index,
            //     exampleIndex: 0
            // })}
            style={{
                position: "absolute",
                top: "5px",
                right: "5px",
                padding: "5px 10px",
                backgroundColor: theme.primaryContainer,
                color: theme.primaryText,
                borderRadius: "5px",
                border: "none",
                cursor: "pointer",
                zIndex: 292
            }}
            title="Run Code"
        >
            <FaPlay style={{ marginRight: "5px" }} /> Run
        </button>

        <div style={{
            height: `${lines * 20 + 20}px`,
            overflow: "hidden",
            display: "flex",
            flexDirection: "row"
        }}>
            <div
                id="editor-container"
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


export default RequestCodeComponent;