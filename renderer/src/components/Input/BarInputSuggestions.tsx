import React, { useContext, useState, useEffect } from 'react';
import { getThemeColors } from '../../themes/getThemeColors';
import { ConfigurationContext } from '../../contexts/configurtionContext';

interface InputWithSuggestionsProps {
    suggestions: string[];
    placeholder?: string;
    onSuggestionSelect?: (suggestion: string) => void;
    value: string;
    setValue: React.Dispatch<string>;
    background?: string
}

const BarInputSuggestions: React.FC<InputWithSuggestionsProps> = ({
    suggestions,
    placeholder = ' Type to search...',
    onSuggestionSelect,
    value, setValue,
    background
}) => {
    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark);

    const [filteredSuggestions, setFilteredSuggestions] = useState<string[]>([]);
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [highlightedIndex, setHighlightedIndex] = useState<number>(-1);
    const [isFocused, setIsFocused] = useState(false);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setValue(value);
        setHighlightedIndex(-1);

        if (value) {
            const filtered = suggestions.filter((suggestion) =>
                suggestion.toLowerCase().includes(value.toLowerCase())
            );
            setFilteredSuggestions(filtered);
            setShowSuggestions(true);
        } else {
            setFilteredSuggestions([]);
            setShowSuggestions(false);
        }
    };

    const handleSuggestionClick = (suggestion: string) => {
        setValue(suggestion);
        setShowSuggestions(false);
        if (onSuggestionSelect) {
            onSuggestionSelect(suggestion);
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'ArrowDown' && filteredSuggestions.length > 0) {
            setHighlightedIndex((prevIndex) =>
                prevIndex < filteredSuggestions.length - 1 ? prevIndex + 1 : 0
            );
        } else if (e.key === 'ArrowUp' && filteredSuggestions.length > 0) {
            setHighlightedIndex((prevIndex) =>
                prevIndex > 0 ? prevIndex - 1 : filteredSuggestions.length - 1
            );
        } else if (e.key === 'Enter' && highlightedIndex >= 0) {
            handleSuggestionClick(filteredSuggestions[highlightedIndex]);
        } else if (e.key === 'Escape') {
            setShowSuggestions(false);
        }
    };

    useEffect(() => {
        if (highlightedIndex >= 0 && highlightedIndex < filteredSuggestions.length) {
            const element = document.getElementById(`suggestion-${highlightedIndex}`);
            element?.scrollIntoView({ block: 'nearest' });
        }
    }, [highlightedIndex]);

    return (
        <div style={{ position: 'relative', width: '100%', background: background ?? theme.alternativeContainer }}>
            <input
                type="text"
                value={value}
                onChange={handleInputChange}
                onKeyDown={handleKeyDown}
                onFocus={() => setIsFocused(true)}
                onBlur={() => setIsFocused(false)}
                placeholder={placeholder}
                style={{
                    width: '100%',
                    padding: '4px',
                    fontSize: `${config.fontSize * 1.1}px`,
                    borderRadius: '4px',
                    border: 'none',
                    outline: 'none',
                    backgroundColor: 'transparent',
                    color: theme.generalText,
                }}
            />
            {/* Active indicator bar */}
            <div
                style={{
                    height: isFocused ? '2px' : '1px',
                    backgroundColor: isFocused ? theme.primaryBorder : theme.secondaryBorder,
                    transition: 'height 0.3s',
                }}
            ></div>
            {showSuggestions && filteredSuggestions.length > 0 && (
                <ul
                    style={{
                        position: 'absolute',
                        top: '100%',
                        left: 0,
                        width: '100%',
                        maxHeight: '150px',
                        overflowY: 'auto',
                        backgroundColor: theme.generalText,
                        border: `1px solid ${theme.primaryBorder}`,
                        borderTop: "none",
                        borderRadius: '0 0 4px 4px',
                        listStyle: 'none',
                        margin: 0,
                        padding: '0',
                        zIndex: 1000,
                    }}
                >
                    {filteredSuggestions.map((suggestion, index) => (
                        <li
                            id={`suggestion-${index}`}
                            key={index}
                            onClick={() => handleSuggestionClick(suggestion)}
                            style={{
                                padding: '8px',
                                cursor: 'pointer',
                                backgroundColor:
                                    highlightedIndex === index
                                        ? theme.highlightContainer
                                        : theme.generalContainer,
                                color:
                                    highlightedIndex === index
                                        ? theme.highlightText
                                        : theme.generalText,
                                borderBottom: `1px solid ${theme.secondaryBorder}`,
                            }}
                            onMouseEnter={(e) => {
                                e.currentTarget.style.backgroundColor = theme.highlightContainer;
                                e.currentTarget.style.color = theme.highlightText;
                            }}
                            onMouseLeave={(e) => {
                                e.currentTarget.style.backgroundColor = theme.generalContainer;
                                e.currentTarget.style.color = theme.generalText;
                            }}
                        >
                            {suggestion}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default BarInputSuggestions;
