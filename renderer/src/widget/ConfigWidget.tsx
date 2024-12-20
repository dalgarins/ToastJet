import React, { useContext, useState } from "react"
import { ConfigurationContext } from "../contexts/configurtionContext"
import { getThemeColors } from "../themes/getThemeColors"
import { FiChevronDown, FiChevronUp } from "react-icons/fi"
import SecondaryTopBar from "../components/Topbar/SecondaryTopBar"
import VariableWidget from "./configWidgets/variableWidget"
import EnvironmentWidget from "./configWidgets/environmentWidget"
import FunctionWidget from "./configWidgets/functionWidget"
import { SettingsWidget } from "./configWidgets/settingsWidget"


export default function ConfigWidget() {
    let config = useContext(ConfigurationContext)
    let theme = getThemeColors(config.isDark)
    let [isExpanded, setIsExpanded] = useState(false)

    let [conf, setConf] = useState(0)

    function toggleExpansion() {
        setIsExpanded(!isExpanded);
    }


    return <div style={{
        border: `1px solid ${theme.simpleBorder}`,
        borderRadius: '5px',
        color: theme.generalText,
        padding: '8px 5px',
        backgroundColor: 'transparent',
    }}>
        <div style={{
            display: 'flex',
            flexDirection: 'row',
            justifyContent: 'start',
            gap: '10px',
        }}>
            <div
                style={{
                    marginRight: '20px',
                    width: "24px",
                    height: "24px",
                    border: `solid 1px ${theme.primaryContainer}`,
                    borderRadius: '50%',
                    backgroundColor: theme.primaryContainer,
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    cursor: 'pointer',
                    transition: 'transform 0.2s, color 0.2s',
                }}
                onClick={toggleExpansion}
                onMouseEnter={(e) => {
                    (e.currentTarget as HTMLElement).style.color = theme.hoverText;
                    (e.currentTarget as HTMLElement).style.transform = 'scale(1.1)';
                }}
                onMouseLeave={(e) => {
                    (e.currentTarget as HTMLElement).style.color = 'inherit';
                    (e.currentTarget as HTMLElement).style.transform = 'scale(1)';
                }}
            >
                {isExpanded ? <FiChevronUp size={20} color={theme.primaryText} /> : <FiChevronDown size={20} color={theme.primaryText} />}
            </div>
            <div
                style={{
                    color: theme.primaryContainer,
                    fontSize: '1.25em',
                    fontWeight: 'bold',
                }}
            >Configurations</div>
        </div>
        {isExpanded &&
            <React.Fragment>
                <SecondaryTopBar
                    items={[{
                        name: "Variables",
                        msg: 0
                    }, {
                        name: "Environment Variables",
                        msg: 0
                    }, {
                        name: "Functions",
                        msg: 0
                    }, {
                        name: "Settings",
                        msg: 0
                    }, {
                        name: "Cookies",
                        msg: 0
                    }]}
                    selectedIndex={conf}
                    onSelect={setConf}
                />
                {conf === 0 && <VariableWidget />}
                {conf === 1 && <EnvironmentWidget />}
                {conf === 2 && <FunctionWidget />}
                {conf === 3 && <SettingsWidget />}
            </React.Fragment>
        }
    </div>
}