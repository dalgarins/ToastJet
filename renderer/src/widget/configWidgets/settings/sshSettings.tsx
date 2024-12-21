import React, { useContext, useState } from "react";
import { getThemeColors } from "../../../themes/getThemeColors";
import { ConfigurationContext } from "../../../contexts/configurtionContext";
import CustomCheckbox from "../../../components/Input/CheckBox";
import BarInputSuggestions from "../../../components/Input/BarInputSuggestions";

export default function SSHSettings() {
    const [isSSHEnabled, setIsSSHEnabled] = useState(false);
    const [sshAddress, setSshAddress] = useState("");
    const [sshPort, setSshPort] = useState("");
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [bypassUrls, setBypassUrls] = useState<string[]>([""]);
    let config = useContext(ConfigurationContext)
    let theme = getThemeColors(config.isDark)

    const handleAddBypassUrl = () => {
        if (bypassUrls.length < 5) { // Limit the number of bypass URLs
            setBypassUrls([...bypassUrls, ""]); // Add a new empty input
        }
    };

    const handleBypassUrlChange = (index: number, value: string) => {
        const updatedUrls = [...bypassUrls];
        updatedUrls[index] = value;
        setBypassUrls(updatedUrls);
    };

    const handleRemoveBypassUrl = (index: number) => {
        if (bypassUrls.length > 1) { // Ensure at least one URL remains
            setBypassUrls(bypassUrls.filter((_, i) => i !== index));
        }
    };

    return (
        <div style={{ padding: "10px" }}>
            <h3>SSH Settings</h3>
            <div style={{ marginBottom: "10px", display: 'flex', alignItems: 'center' }}>
                <CustomCheckbox
                    checked={isSSHEnabled}
                    onChange={(e) => setIsSSHEnabled(e)}
                />
                <span style={{ marginLeft: "5px" }}>Enable SSH</span>
            </div>
            {isSSHEnabled && (
                <div>
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: "10px" }}>
                        <label style={{ width: '150px' }}>Address:</label>
                        <BarInputSuggestions
                            value={sshAddress}
                            setValue={setSshAddress}
                            suggestions={[]} // Provide suggestions if needed
                            placeholder="Enter address"
                            background="transparent"
                        />
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: "10px" }}>
                        <label style={{ width: '150px' }}>Port:</label>
                        <BarInputSuggestions
                            value={sshPort}
                            setValue={setSshPort}
                            suggestions={[]}
                            placeholder="Enter port"
                            background="transparent"
                        />
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: "10px" }}>
                        <label style={{ width: '150px' }}>Username:</label>
                        <BarInputSuggestions
                            value={username}
                            setValue={setUsername}
                            suggestions={[]}
                            placeholder="Enter user name"
                            background="transparent"
                        />
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: "10px" }}>
                        <label style={{ width: '150px' }}>Password:</label>
                        <BarInputSuggestions
                            value={password}
                            setValue={setPassword}
                            suggestions={[]}
                            placeholder="Enter password"
                            background="transparent"
                        />
                    </div>
                </div>
            )}
        </div>
    );
}