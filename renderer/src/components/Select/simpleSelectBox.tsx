import React, { useState, useContext } from 'react';
import { getThemeColors } from '../../themes/getThemeColors';
import { ConfigurationContext } from '../../contexts/configurtionContext';

interface Option {
    value: any;
    label: string;
    color?: string;
}

interface SelectBoxProps {
    options: Option[];
    placeholder?: string;
    flex: number;
    selectedValue: any;
    setSelectedValue: React.Dispatch<any>;
}

const SimpleSelectBox: React.FC<SelectBoxProps> = ({
    options,
    placeholder,
    flex,
    selectedValue,
    setSelectedValue,
}) => {
    const [isOpen, setIsOpen] = useState(false); // State to control the dropdown open/close
    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark);

    const handleOptionSelect = (option: Option) => {
        setSelectedValue(option.value); // Set the selected value
        setIsOpen(false); // Close the dropdown
    };

    return (
        <div style={{
            position: 'relative',
            flexGrow: flex,
            display: 'flex',
            flexDirection: 'column',
            width: '10px', 
        }}>
            <div
                onClick={() => setIsOpen(!isOpen)} // Toggle dropdown on click
                style={{
                    padding: '2px 8px', // Reduced padding for smaller input box
                    border: `2px solid ${isOpen ? theme.primaryBorder : 'transparent'}`,
                    borderRadius: '4px',
                    backgroundColor: '',
                    color: options[selectedValue]?.color ?? theme.generalText,
                    fontSize: '14px', // Reduced font size
                    cursor: 'pointer',
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                }}
            >
                <span>{options[selectedValue]?.label || placeholder}</span>
                <span style={{ fontSize: '18px' }}>{isOpen ? '▲' : '▼'}</span>
            </div>
            {isOpen && (
                <div style={{
                    position: 'absolute',
                    top: '100%',
                    left: 0,
                    right: 0,
                    backgroundColor: theme.generalContainer,
                    borderRadius: '4px',
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
                    zIndex: 1,
                }}>
                    {options.map((option, index) => (
                        <div
                            key={index}
                            style={{
                                padding: '2px 10px', // Reduced padding for smaller options
                                cursor: 'pointer',
                                borderBottom: index < options.length - 1 ? `1px solid ${theme.simpleBorder}` : 'none',
                                backgroundColor: selectedValue === option.value ? theme.highlightContainer : 'transparent',
                                fontSize: '12px',
                                color: option?.color ?? theme.generalText
                            }}
                            onMouseDown={() => handleOptionSelect(option)}
                        >
                            {option.label}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default SimpleSelectBox;
