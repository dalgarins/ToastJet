import React, { useContext, useState } from "react";
import { getThemeColors } from "../../themes/getThemeColors";
import { ConfigurationContext } from "../../contexts/configurtionContext";

export interface TabInterface {
    title: string;
    action: () => void;
}

const BlockTabBar = ({ tabs }: { tabs: TabInterface[] }) => {
    const [selectedIndex, setSelectedIndex] = useState(0);
    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark);

    return (
        <div
            style={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "center",
                alignItems: "center",
                backgroundColor: theme.generalContainer,
                boxShadow: "0px 2px 6px rgba(0, 0, 0, 0.1)",
                borderRadius: "8px",
            }}
        >
            {tabs.map((tab, index) => {
                const isSelected = index === selectedIndex;

                return (
                    <div
                        key={index}
                        onClick={() => {
                            setSelectedIndex(index);
                            tab.action();
                        }}
                        style={{
                            padding: "6px 12px", // Reduced padding
                            cursor: "pointer",
                            fontWeight: isSelected ? "bold" : "normal",
                            fontSize: "12px", // Smaller font size
                            textAlign: "center",
                            borderRadius: "4px", // Rounded corners
                            backgroundColor: isSelected
                                ? theme.primaryContainer
                                : theme.alternativeContainer,
                            color: isSelected ? theme.primaryText : theme.alternativeText,
                            transition: "all 0.3s ease",
                            boxShadow: isSelected
                                ? "0px 2px 4px rgba(0, 0, 0, 0.2)"
                                : "none",
                        }}
                    >
                        {tab.title}
                    </div>
                );
            })}
        </div>
    );
};

export default BlockTabBar;