import React, { useContext, useState } from 'react';
import { getThemeColors } from '../../themes/getThemeColors';
import { ConfigurationContext } from '../../contexts/configurtionContext';

interface CheckboxProps {
    checked?: boolean;
    onChange?: (checked: boolean) => void;
    disabled?: boolean;
}

const CustomCheckbox: React.FC<CheckboxProps> = ({ checked = false, onChange, disabled = false }) => {
    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark);

    const [isChecked, setIsChecked] = useState(checked);

    const handleCheckboxChange = () => {
        const newCheckedState = !isChecked;
        setIsChecked(newCheckedState);
        if (onChange) {
            onChange(newCheckedState);
        }
    };

    const styles = {
        checkboxInput: {
            display: 'none',
        },
        customCheckbox: {
            width: '15px',
            height: '15px',
            borderRadius: '3px',
            border: `2px solid ${theme.primaryBorder}`,
            backgroundColor: isChecked ? theme.primaryContainer : theme.generalContainer,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            transition: 'background-color 0.3s, border-color 0.3s',
        },
        checkmark: {
            color: theme.primaryText,
            fontSize: '18px',
            fontWeight: "bold",
        },
    };

    return (
        <label style={{
            display: 'flex',
            alignItems: 'center',
            cursor: 'pointer',
            userSelect: 'none',
        }}>
            <input
                type="checkbox"
                checked={isChecked}
                onChange={handleCheckboxChange}
                style={styles.checkboxInput}
                disabled={disabled}
            />
            <span style={styles.customCheckbox}>
                {isChecked && <span style={styles.checkmark}>✓</span>}
            </span>
        </label>
    );
};

export default CustomCheckbox;