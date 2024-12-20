import React, { useContext, useEffect, useRef, useState } from "react";
import { loader } from "@monaco-editor/react";
import { editor } from "monaco-editor";
import { getThemeColors } from "../../themes/getThemeColors";
import { ConfigurationContext } from "../../contexts/configurtionContext";
import { getVsCodeTheme } from "../../themes/vsCodeThemes";

const JsonXmlRenderer = ({ value, type = "json", key }: {
    value: string;
    type: "json" | "xml" | "text" | "html" | "javascript";
    key: string | number;
}) => {
    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark)
    const editorRef = useRef<editor.IStandaloneCodeEditor | null>(null);
    const containerRef = useRef<HTMLDivElement | null>(null);
    const [minHeight, setMinHeight] = useState(400)
    useEffect(() => {
        loader.init().then((monaco) => {
            monaco.editor.defineTheme('myTransparentTheme', getVsCodeTheme(config.isDark));

            if (!containerRef.current) return;

            const m = monaco.editor.create(containerRef.current, {
                value,
                language: type,
                automaticLayout: true,
                lineHeight: 20,
                scrollBeyondLastLine: false,
                theme: "myTransparentTheme",
                minimap: {
                    enabled: false,
                },
                scrollbar: {
                    verticalScrollbarSize: 5

                },
                readOnly: true,
                wordWrap: "on",
            });

            editorRef.current = m;

            const handleResize = () => {
                formatDocument(m)
            };

            window.addEventListener('resize', handleResize);
            formatDocument(m);
            return () => {
                if (editorRef.current) {
                    editorRef.current.dispose();
                }
                window.removeEventListener('resize', handleResize);
            };
        });
    }, [value, type, config.isDark]); // Recreate editor only when these change

    const formatDocument = async (editor: editor.IStandaloneCodeEditor) => {
        await editor.getAction("editor.action.formatDocument")?.run()
        const contentHeight = editor.getContentHeight();
        setMinHeight(Math.min(contentHeight * 1.05, 400))
        editor.layout();
    };

    return (
        <div style={{
            height: "100%",
            minHeight: `${minHeight}px`,
            overflow: "hidden",
            width: "100%",
            background: theme.generalContainer
        }}>
            <div
                ref={containerRef}
                style={{
                    width: "100%",
                    height: "100%",
                    minHeight: `${minHeight}px`
                }}
            />
        </div>
    );
};

export default JsonXmlRenderer;
