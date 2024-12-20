import React, { useContext, useState, useEffect, useRef } from 'react';
import { getThemeColors } from '../../themes/getThemeColors';
import { ConfigurationContext } from '../../contexts/configurtionContext';

interface Suggestion {
    name: string;
}

interface SimpleInputProps {
    suggestions: Suggestion[];
    placeholder?: string;
    flex: number;
    inputValue: string;
    setInputValue: React.Dispatch<string>;
}

const SimpleInputSuggestions: React.FC<SimpleInputProps> = ({
    suggestions,
    placeholder,
    flex,
    inputValue,
    setInputValue,
}) => {
    const [filteredSuggestions, setFilteredSuggestions] = useState<Suggestion[]>([]);
    const [selectedIndex, setSelectedIndex] = useState<number>(-1);
    const [isFocused, setIsFocused] = useState(false);
    const [isHovered, setIsHovered] = useState(false);
    const suggestionListRef = useRef<HTMLDivElement | null>(null);
    const suggestionItemsRef = useRef<(HTMLDivElement | null)[]>([]);

    let config = useContext(ConfigurationContext);
    let theme = getThemeColors(config.isDark);

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setInputValue(value);
        if (value) {
            const filtered = suggestions.filter(suggestion =>
                suggestion.name.toLowerCase().includes(value.toLowerCase())
            );
            setFilteredSuggestions(filtered);
        } else {
            setFilteredSuggestions([]);
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.ctrlKey && e.key === ' ') {
            if (filteredSuggestions.length > 0) {
                setFilteredSuggestions([]);
            } else {
                const filtered = suggestions.filter(suggestion =>
                    suggestion.name.toLowerCase().includes(inputValue.toLowerCase())
                );
                setFilteredSuggestions(filtered);
            }
            e.preventDefault();
            return;
        }

        if (filteredSuggestions.length === 0) return;

        if (e.key === 'ArrowDown') {
            setSelectedIndex(prevIndex =>
                prevIndex < filteredSuggestions.length - 1 ? prevIndex + 1 : prevIndex
            );
        } else if (e.key === 'ArrowUp') {
            setSelectedIndex(prevIndex => (prevIndex > 0 ? prevIndex - 1 : prevIndex));
        } else if (e.key === 'Enter' && selectedIndex !== -1) {
            setInputValue(filteredSuggestions[selectedIndex].name);
            setFilteredSuggestions([]);
        } else if (e.key === 'Escape') {
            setFilteredSuggestions([]);
        }
    };

    useEffect(() => {
        if (suggestionListRef.current && selectedIndex !== -1 && suggestionItemsRef.current[selectedIndex]) {
            const selectedItem = suggestionItemsRef.current[selectedIndex];
            if (selectedItem) {
                selectedItem.scrollIntoView({
                    behavior: 'smooth',
                    block: 'nearest',
                });
            }
        }
    }, [selectedIndex, filteredSuggestions]);

    // Reset selected index when suggestions change
    useEffect(() => {
        setSelectedIndex(-1);
    }, [filteredSuggestions]);

    return (
        <div style={{
            position: 'relative',
            flexGrow: flex,
            display: 'flex',
            flexDirection: 'row',
            width: "10px",
        }}>
            <input
                type="text"
                value={inputValue}
                onChange={handleInputChange}
                onFocus={() => setIsFocused(true)}
                onBlur={() => setIsFocused(false)}
                onKeyDown={handleKeyDown}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
                placeholder={placeholder}
                style={{
                    flexGrow: 1,
                    width: '100%',
                    padding: '6px 8px',
                    border: `2px solid ${isFocused ? theme.primaryBorder : isHovered ? theme.secondaryBorder : 'transparent'}`,
                    borderRadius: '4px',
                    backgroundColor: isFocused ? theme.generalContainer : 'transparent',
                    outline: 'none',
                    color: theme.generalText,
                    fontSize: '14px',
                    transition: 'border 0.2s ease-in-out', // Smooth border transition
                }}
            />
            {filteredSuggestions.length > 0 && (
                <div
                    ref={suggestionListRef}
                    style={{
                        position: 'absolute',
                        top: '100%',
                        left: 0,
                        right: 0,
                        backgroundColor: theme.generalContainer,
                        borderRadius: '4px',
                        boxShadow: '0 2px 8px rgba(0, 0, 0, 0.1)',
                        zIndex: 1,
                        maxHeight: '200px',
                        overflowY: 'auto', // Enable scrolling
                        padding: '5px 0',
                    }}
                >
                    {filteredSuggestions.map((suggestion, index) => (
                        <div
                            key={index}
                            ref={(el) => {
                                suggestionItemsRef.current[index] = el
                            }}
                            style={{
                                padding: '6px 10px',
                                cursor: 'pointer',
                                fontSize: '12px',
                                borderBottom: index < filteredSuggestions.length - 1 ? `1px solid ${theme.simpleBorder}` : 'none',
                                backgroundColor: selectedIndex === index ? theme.alternativeContainer : 'transparent', // Highlight selected item
                            }}
                            onMouseDown={() => {
                                setInputValue(suggestion.name);
                                setFilteredSuggestions([]);
                            }}
                            onMouseEnter={() => setSelectedIndex(index)} // Highlight suggestion on hover
                        >
                            {suggestion.name}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default SimpleInputSuggestions;
