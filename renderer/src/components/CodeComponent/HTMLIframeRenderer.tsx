import React, { useContext, useState } from "react";
import { getThemeColors } from "../../themes/getThemeColors";
import JsonXmlRenderer from "./JsonXmlRenderer";
import { ConfigurationContext } from "../../contexts/configurtionContext";
import getNonce from "../../utilities/getNonce";

export default function HTMLIframeRenderer({ htmlContent }: { htmlContent: string }) {
    const [viewMode, setViewMode] = useState<'raw' | 'preview'>('preview');
    const [copied, setCopied] = useState(false);
    const config = useContext(ConfigurationContext);
    const themeStyle = getThemeColors(config.isDark);

    const handleCopy = () => {
        navigator.clipboard.writeText(htmlContent).then(() => {
            setCopied(true);
            setTimeout(() => setCopied(false), 2000); // Reset copied state after 2 seconds
        });
    };

    return (
        <div style={{ overflowY: "scroll", display: "flex", justifyContent: "center", padding: "20px" }}>
            <div style={{
                width: "95%",
                backgroundColor: themeStyle.generalContainer,
                borderRadius: "8px",
                boxShadow: `0px 4px 12px ${themeStyle.alternativeContainer}`,
                padding: "15px"
            }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "15px" }}>
                    <div
                        onClick={() => setViewMode(viewMode === 'preview' ? 'raw' : 'preview')}
                        style={{
                            backgroundColor: themeStyle.primaryContainer,
                            color: themeStyle.primaryText,
                            padding: "8px 12px",
                            borderRadius: "4px",
                            cursor: "pointer",
                            transition: "background-color 0.3s",
                        }}
                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = themeStyle.hoverContainer}
                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = themeStyle.primaryContainer}
                    >
                        {viewMode === 'preview' ? 'Raw' : 'Preview'}
                    </div>

                    <span
                        style={{ cursor: "pointer", display: "inline-flex", alignItems: "center", padding: "8px" }}
                        onClick={handleCopy}
                        title="Copy to clipboard"
                    >
                        <span style={{ marginRight: "5px" }}>{copied ? '✅ Copied!' : '📋 Copy'}</span>
                    </span>
                </div>

                {viewMode === 'preview' ? (
                    <iframe
                        style={{
                            width: "100%",
                            height: "500px",
                            borderRadius: "4px",
                            border: `1px solid ${themeStyle.simpleBorder}`,
                        }}
                        srcDoc={htmlContent}
                        sandbox="allow-scripts allow-same-origin"
                        title="Rendered HTML Content"
                    />
                ) : (
                    <pre style={{
                        whiteSpace: 'pre-wrap',
                        fontFamily: 'monospace',
                        padding: '10px',
                        backgroundColor: themeStyle.alternativeContainer,
                        borderRadius: '4px',
                        overflowX: 'auto',
                    }}>
                        <JsonXmlRenderer type="html" value={htmlContent} key={`html-${getNonce()}`} />
                    </pre>
                )}
            </div>
        </div>
    );
};