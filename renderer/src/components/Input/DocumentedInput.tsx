import React, { useContext, useEffect, useRef, useState } from "react";
import { getThemeColors } from "../../themes/getThemeColors";
import { ConfigurationContext } from "../../contexts/configurtionContext";

type Suggestion = {
    name: string;
    description: string;
    example: string;
};

const DocumentedInput = ({
    suggestions,
    placeholder,
    inputValue,
    setInputValue
}: {
    suggestions: Suggestion[],
    placeholder: string,
    inputValue: string;
    setInputValue: React.Dispatch<string>;
}) => {
    const [filteredSuggestions, setFilteredSuggestions] = useState<Suggestion[]>([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [selectedIndex, setSelectedIndex] = useState(0);
    const [isFocused, setIsFocused] = useState(false);
    const textareaRef = useRef<HTMLTextAreaElement | null>(null);

    const suggestionRefs = useRef<(HTMLDivElement | null)[]>([]);

    let configuration = useContext(ConfigurationContext);
    let theme = getThemeColors(configuration.isDark);

    const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        const value = e.target.value;
        setInputValue(value);

        // Adjust textarea height based on content
        if (textareaRef.current) {
            textareaRef.current.style.height = 'auto';
            textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;
        }

        // Get caret position
        const caretPosition = e.target.selectionStart;

        // Check for placeholder pattern
        const placeholderIndex = value.substring(0, caretPosition).lastIndexOf("${");
        if (placeholderIndex !== -1) {
            const afterPlaceholder = value.substring(placeholderIndex + 2, caretPosition);
            if (afterPlaceholder.endsWith("}")) {
                setShowDropdown(false);
                setSelectedIndex(-1);
            } else {
                const filterText = afterPlaceholder;
                const filtered = suggestions.filter(suggestion =>
                    suggestion.name.toLowerCase().includes(filterText.toLowerCase())
                );
                setFilteredSuggestions(filtered);
                if (filtered.length > 0) {
                    setSelectedIndex(0);
                }
                setShowDropdown(filtered.length > 0);
            }
        } else {
            setShowDropdown(false);
            setSelectedIndex(-1);
        }
    };

    const handleSuggestionClick = (suggestion: Suggestion) => {
        if (textareaRef.current) {
            const currentValue = inputValue;
            const caretPosition = textareaRef.current.selectionStart;
            const placeholderIndex = currentValue.substring(0, caretPosition).lastIndexOf("${");

            console.log("sub string here",
                currentValue,
                caretPosition,
                placeholderIndex,
                currentValue.slice(0, placeholderIndex + 2),
                currentValue.slice(caretPosition),
                suggestion.name,
                currentValue.slice(0, placeholderIndex + 2) + suggestion.name + "}" + currentValue.slice(caretPosition)
            )


            let newValue;

            if (placeholderIndex !== -1) {
                newValue = currentValue.slice(0, placeholderIndex + 2) + suggestion.name + "()}" + currentValue.slice(caretPosition);
            } else {
                newValue = currentValue + suggestion.name + "()";
            }

            setInputValue(newValue);
            setShowDropdown(false);
            setSelectedIndex(-1);

            textareaRef.current.style.height = 'auto';
            textareaRef.current.style.height = `${textareaRef.current.scrollHeight}px`;

            requestAnimationFrame(() => {
                if (textareaRef.current != null) {
                    textareaRef.current.selectionStart = textareaRef.current.selectionEnd = placeholderIndex + suggestion.name.length + 5;
                    textareaRef.current.focus();
                }
            });
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent) => {
        if (e.key === "ArrowDown") {
            e.preventDefault();
            if (selectedIndex < filteredSuggestions.length - 1) {
                setSelectedIndex(selectedIndex + 1);
            }
        } else if (e.key === "ArrowUp") {
            e.preventDefault();
            if (selectedIndex > 0) {
                setSelectedIndex(selectedIndex - 1);
            }
        } else if (e.key === "Enter") {
            e.preventDefault();
            if (selectedIndex >= 0 && selectedIndex < filteredSuggestions.length) {
                handleSuggestionClick(filteredSuggestions[selectedIndex]);
            }
        } else if (e.key === "Escape") {
            setShowDropdown(false);
        } else if (e.ctrlKey && e.key === " ") {
            e.preventDefault();
            setShowDropdown(true);
        }
        if (showDropdown && filteredSuggestions.length > 0) {
            const activeSuggestionRef = suggestionRefs.current[selectedIndex];
            if (activeSuggestionRef) {
                activeSuggestionRef.scrollIntoView({
                    behavior: 'smooth',
                    block: 'center',
                });
            }
        }
    };

    useEffect(() => {
        const handleClickOutside = (e: any) => {
            if (textareaRef.current && !textareaRef.current.contains(e.target)) {
                setShowDropdown(false);
                setSelectedIndex(-1);
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

    const renderInputValue = () => {
        const parts = inputValue.split(/(\$\{.*?\}|\#\{.*?\})/g);

        return parts.map((part, index) => {
            if (!part) return null; // Skip empty parts

            // If the part is a `${}` pattern, style it with infoColor
            if (part.startsWith("${") && part.endsWith("}")) {
                return (
                    <span key={index} style={{ color: theme.infoColor }}>
                        {part}
                    </span>
                );
            }

            // If the part is a `#{}` pattern, style it with secondaryColor
            if (part.startsWith("#{") && part.endsWith("}")) {
                return (
                    <span key={index} style={{ color: theme.accentColor }}>
                        {part}
                    </span>
                );
            }

            // If it's just regular text, return it without any special styling
            return <span key={index}>{part}</span>;
        });
    };


    return (
        <div style={{ position: 'relative', width: '100%', color: "white" }}>
            <div style={{
                display: "flex",
                alignItems: "flex-start",
                border: `1px solid ${isFocused ? theme.primaryBorder : theme.simpleBorder}`,
                borderRadius: '4px',
                padding: '8px', // Increase padding for better spacing
                transition: 'min-height 0.2s ease',
                backgroundColor: 'transparent',
                color: theme.generalText
            }}>
                <div style={{ flexGrow: 1 }}>
                    <div style={{
                        color: theme.generalText,
                        minHeight: '22px',
                        lineHeight: '1.4',  // Increased line height
                        letterSpacing: '0.5px',
                        fontSize: '16px',  // Increased font size
                        fontFamily: 'Arial, sans-serif',
                    }}>
                        {renderInputValue()}
                    </div>
                    <textarea
                        ref={textareaRef}
                        value={inputValue}
                        onChange={handleInputChange}
                        onKeyDown={handleKeyDown}
                        onFocus={() => setIsFocused(true)}
                        onBlur={() => setIsFocused(false)}
                        style={{
                            fontSize: '16px',  // Increased font size
                            fontFamily: 'Arial, sans-serif',
                            lineHeight: '1.4',  // Increased line height
                            letterSpacing: '0.5px',
                            position: "absolute",
                            top: 7,
                            left: 7,
                            right: 7,
                            bottom: 7,
                            color: "transparent",
                            caretColor: theme.generalText,
                            border: "none",
                            outline: "none",
                            backgroundColor: "transparent",
                            resize: 'none',
                            overflowY: 'hidden',
                        }}
                        placeholder={placeholder}
                    />
                </div>
            </div>

            {showDropdown && filteredSuggestions.length !== 0 && (
                <div style={{
                    position: 'absolute',
                    top: '100%',
                    left: 0,
                    backgroundColor: theme.generalContainer,
                    width: '100%',
                    boxShadow: '0 3px 4px rgba(0, 0, 0, 0.5)',
                    zIndex: 1000,
                    borderRadius: '6px',
                    display: 'flex',
                    flexDirection: 'row',
                    minHeight: '200px',
                    maxHeight: '300px',
                    alignItems: 'stretch',
                }}>
                    <div style={{
                        flexGrow: 1,
                        width: "10px",
                        maxWidth: "200px",
                        overflowY: 'scroll',
                    }}>
                        {filteredSuggestions.map((suggestion, index) => (
                            <div
                                ref={(x) => {
                                    suggestionRefs.current[index] = x;
                                }}
                                key={index}
                                onMouseDown={() => handleSuggestionClick(suggestion)}
                                onMouseEnter={() => setSelectedIndex(index)}
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    padding: '8px 12px',
                                    cursor: 'pointer',
                                    backgroundColor: selectedIndex === index ? theme.highlightContainer : 'transparent',
                                    transition: 'background-color 0.2s ease',
                                    borderBottom: `1px solid ${theme.simpleBorder}`,
                                    fontSize: '14px',
                                    color: theme.generalText,
                                    whiteSpace: 'nowrap',
                                }}
                            >
                                <div style={{
                                    width: '24px',
                                    height: '24px',
                                    borderRadius: '50%',
                                    backgroundColor: theme.highlightContainer,
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    marginRight: '8px',
                                    fontWeight: 'bold',
                                    color: theme.highlightText,
                                    fontSize: '12px'
                                }}>
                                    {suggestion.name.charAt(0).toUpperCase()}
                                </div>
                                <span>{suggestion.name}</span>
                            </div>
                        ))}
                    </div>

                    <div style={{
                        flexGrow: 2,
                        width: "10px",
                        padding: '4px',
                        display: 'flex',
                        flexDirection: 'column',
                        justifyContent: 'flex-start',
                        gap: '3px',
                        color: theme.alternativeText,
                        maxHeight: '200px',
                        borderLeft: `2px solid ${theme.secondaryBorder}`,
                    }}>
                        <div style={{ fontSize: '16px', fontWeight: '700' }}>
                            {suggestions[selectedIndex]?.description}
                        </div>
                        <pre style={{ fontSize: '14px', color: theme.generalText }}>
                            {suggestions[selectedIndex]?.example}
                        </pre>
                    </div>
                </div>
            )}
        </div>
    );
};

export default DocumentedInput;
