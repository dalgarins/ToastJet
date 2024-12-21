import React, { useContext, useEffect, useState } from "react";
import { getThemeColors } from "../../themes/getThemeColors";
import { FaCode } from "react-icons/fa";
import { FaDesktop } from "react-icons/fa6";
import { ConfigurationContext } from "../../contexts/configurtionContext";
import BarInputSuggestions from "../Input/BarInputSuggestions";
import { RequestContext } from "../../contexts/requestContext";

export default function PrimaryTopBar({
    isVerticalView, setIsVerticalView, isCodeView, setIsCodeView
}: {
    isVerticalView: boolean,
    setIsVerticalView: (a: boolean) => void,
    isCodeView: boolean,
    setIsCodeView: (a: boolean) => void
}) {
    const configuration = useContext(ConfigurationContext);
    const theme = getThemeColors(configuration.isDark);
    const requestData = useContext(RequestContext)
    let [showVerticalOption, setShowVerticalOption] = useState(true)
    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth <= 1200) {
                setShowVerticalOption(false);
            } else {
                setShowVerticalOption(true)
            }
        };
        handleResize();
        window.addEventListener("resize", handleResize);
        return () => window.removeEventListener("resize", handleResize);
    }, []);

    return (
        <div
            style={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                alignItems: "center",
                borderRadius: "6px",
                backgroundColor: "transparent",
                marginLeft: "5px",
                marginRight: "5px",
                gap: "10px"
            }}
        >
            <BarInputSuggestions
                suggestions={[]}
                onSuggestionSelect={() => { }}
                placeholder=" Enter name"
                value={requestData.data.name}
                setValue={(value: string) => {
                    requestData.data.name = value;
                    requestData.setData({ ...requestData.data })
                }} />
            <div style={{ display: "flex", gap: "10px" }}>
                {showVerticalOption && <button
                    onClick={() => setIsVerticalView(!isVerticalView)}
                    style={{
                        fontSize: "20px",
                        background: theme.alternativeContainer,
                        border: `2px solid ${theme.primaryBorder}`,
                        cursor: "pointer",
                        color: theme.primaryContainer,
                        transition: "color 0.3s ease",
                        display: "flex",
                        alignItems: "center",
                        width: "35px",
                        borderRadius: "7px",
                        height: "35px",
                        justifyContent: "center",
                    }}
                    title={isVerticalView ? "Switch to Horizontal View" : "Switch to Vertical View"}
                    onMouseEnter={(e) => {
                        e.currentTarget.style.color = theme.accentColor
                        e.currentTarget.style.borderColor = theme.accentColor
                    }}
                    onMouseLeave={(e) => {
                        e.currentTarget.style.color = theme.successText
                        e.currentTarget.style.borderColor = theme.primaryBorder
                    }}
                >
                    <span
                        style={{
                            display: "inline-block",
                            transform: isVerticalView ? "rotate(90deg)" : "rotate(0deg)",
                            transition: "transform 0.3s ease",
                            padding: "2px 4px",
                            fontSize: "18px",
                        }}
                    >
                        ][
                    </span>
                </button>}
                <button
                    onClick={() => setIsCodeView(!isCodeView)}
                    style={{
                        fontSize: "16px",
                        background: "none",
                        border: `2px solid ${theme.primaryBorder}`,
                        cursor: "pointer",
                        color: theme.primaryContainer,
                        transition: "color 0.3s ease",
                        gap: "8px",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: 'center',
                        width: "35px",
                        borderRadius: "7px",
                        height: "35px",
                    }}
                    title={isCodeView ? "Switch to GUI View" : "Switch to Code View"}
                    onMouseEnter={(e) => (e.currentTarget.style.color = theme.primaryText)}
                    onMouseLeave={(e) => (e.currentTarget.style.color = theme.primaryContainer)}
                >
                    {isCodeView
                        ? <FaCode />
                        : <FaDesktop />
                    }
                </button>
            </div>
        </div>
    );
}
