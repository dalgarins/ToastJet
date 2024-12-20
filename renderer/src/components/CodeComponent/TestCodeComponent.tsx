import React from "react";
import { useContext, useEffect, useRef, useState } from "react";
import { getThemeColors } from "../../themes/getThemeColors";
import { loader } from "@monaco-editor/react";
import { helperLib } from "../../monaco/helperLib";
import { ConfigurationContext } from "../../contexts/configurtionContext";
import { getVsCodeTheme } from "../../themes/vsCodeThemes";

const TestCodeComponent = ({ value, setValue }: { value: string, setValue: (a: string) => void }) => {
    const config = useContext(ConfigurationContext)
    const theme = getThemeColors(config.isDark)
    const editorRef = useRef<any>(null);
    const [lines, setLines] = useState(20);

    useEffect(() => {
        loader.init().then((monaco) => {
            monaco.languages.typescript.javascriptDefaults.addExtraLib(helperLib);
            monaco.editor.defineTheme('myTransparentTheme', getVsCodeTheme(config.isDark));
            let x = document.getElementById('editor-container')!
            const m = monaco.editor.create(x, {
                value: value.trim() === "" ? `TestFunction((req, res) => {
  //Perform any  check on the request and response
  return {
    error: true | false,
    message: ""
  }
}, {
  name: "CheckForProperty",
  description: "Checks if the request or response contains a specific property",
})


GeneratorFunction(() => {
  const roles = ["admin", "owner", "manager", "user"];
  const randomIndex = Math.floor(Math.random() * roles.length);
  return roles[randomIndex];
}, {
  name: "GenerateRole",
});` : value,
                language: 'javascript',
                automaticLayout: true,
                lineHeight: 20,
                scrollBeyondLastLine: false,
                theme: "myTransparentTheme",
                minimap: {
                    enabled: false
                },
            });

            function scrollEvent(event: WheelEvent) {
                event.preventDefault();
                const scrollAmountY = event.deltaY;
                const scrollAmountX = event.deltaX;
                window.scrollBy({
                    top: scrollAmountY,
                    left: scrollAmountX,
                    behavior: "smooth"
                });
            }

            x.addEventListener("wheel", scrollEvent, {
                capture: true,
            })

            editorRef.current = m;

            m.onDidChangeModelContent((e) => {
                const newValue = m.getValue();
                setValue(newValue);
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

    return <div style={{ width: "100%" }}>
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
                    position: "relative",
                    height: '100%',
                }}
            >

            </div>
        </div>
    </div>
};

export default TestCodeComponent