import React, { useContext, useState } from "react";
import { FaTrashAlt } from "react-icons/fa";
import { getThemeColors } from "../../../themes/getThemeColors"; // Assuming you have a theme provider
import { ConfigurationContext } from "../../../contexts/configurtionContext";
import CustomCheckbox from "../../../components/Input/CheckBox";
import BarInputSuggestions from "../../../components/Input/BarInputSuggestions";


export default function ProxySetting() {
    const [isProxyEnabled, setIsProxyEnabled] = useState(false);
    const [proxyAddress, setProxyAddress] = useState("");
    const [proxyPort, setProxyPort] = useState("");
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
            <h3>Proxy Settings</h3>
            <div style={{ marginBottom: "10px", display: 'flex', alignItems: 'center' }}>
                <CustomCheckbox
                    checked={isProxyEnabled}
                    onChange={(e) => setIsProxyEnabled(e)}
                />
                <span style={{ marginLeft: "5px" }}>Enable Proxy</span>
            </div>
            {isProxyEnabled && (
                <div>
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: "10px" }}>
                        <label style={{ width: '150px' }}>Address:</label>
                        <BarInputSuggestions
                            value={proxyAddress}
                            setValue={setProxyAddress}
                            suggestions={[]} // Provide suggestions if needed
                            placeholder="Enter address"
                            background="transparent"
                        />
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', marginBottom: "10px" }}>
                        <label style={{ width: '150px' }}>Port:</label>
                        <BarInputSuggestions
                            value={proxyPort}
                            setValue={setProxyPort}
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

                    <h4>Bypass URLs</h4>
                    {bypassUrls.map((url, index) => (
                        <div key={index} style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                            <BarInputSuggestions
                                value={url}
                                setValue={(e) => handleBypassUrlChange(index, e)}
                                placeholder="Enter bypass URL"
                                suggestions={[]}
                                background="transparent"
                            />
                            <button onClick={() => handleRemoveBypassUrl(index)} style={{
                                backgroundColor: 'transparent',
                                border: 'none',
                                cursor: 'pointer',
                                marginLeft: '5px'
                            }}>
                                <FaTrashAlt color="red" />
                            </button>
                        </div>
                    ))}
                    <button onClick={handleAddBypassUrl} style={{
                        padding: '5px 10px',
                        backgroundColor: theme.primaryContainer,
                        color: theme.primaryText,
                        border: 'none',
                        borderRadius: '4px',
                        cursor: 'pointer'
                    }}>
                        Add Bypass URL
                    </button>
                </div>
            )}
        </div>
    );
}