import React, { useContext, useState } from "react"
import { getThemeColors } from "../../themes/getThemeColors"
import { ConfigurationContext } from "../../contexts/configurtionContext"
import CustomSelect from "../../components/Select/customSelect"
import ProxySetting from "./settings/proxySettings"
import SshSettings from "./settings/sshSettings"

export function SettingsWidget() {
    let config = useContext(ConfigurationContext)
    let theme = getThemeColors(config.isDark)
    let options = [
        "Proxy",
        "SSH Connection",
    ]
    let [selectedOption, setSelectedOption] = useState(0)
    return <div style={{
        margin: "15px 5px",
        border: `1px solid ${theme.simpleBorder}`,
        borderRadius: '4px',
        padding: "5px 10px"
    }}>
        <div
            style={{
                display: 'flex',
                justifyContent: 'start',
                gap: "10px",
                alignItems: 'center',
            }}
        >
            <div style={{
                color: theme.primaryContainer,
                fontSize: '1.25em',
                fontWeight: 'bold',
            }}>Settings</div>
            <div style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'row',
                justifyContent: 'end',
                alignItems: 'center',
                gap: '10px',
            }}>
                <div style={{
                    width: "300px",
                    display: 'flex',
                }}>
                    <CustomSelect
                        options={options.map((x, i) => ({
                            label: x,
                            value: i,
                        }))}
                        theme={theme}
                        value={selectedOption}
                        onChange={(a) => {
                            setSelectedOption(a)
                        }}
                    />
                </div>
            </div>
        </div>
        <div>
            {selectedOption === 0 && <ProxySetting />}
            {selectedOption === 1 && <SshSettings />}
        </div>
    </div>
}