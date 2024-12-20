import React, { useContext, useState, useEffect } from 'react';
import { getThemeColors } from '../../themes/getThemeColors';
import { ConfigurationContext } from '../../contexts/configurtionContext';

interface InputWithSuggestionsProps {
  suggestions: string[];
  placeholder?: string;
  onSuggestionSelect?: (suggestion: string) => void;
  inputValue: any;
  setInputValue: React.Dispatch<React.SetStateAction<any>>;
}

const InputWithSuggestions: React.FC<InputWithSuggestionsProps> = ({
  suggestions,
  placeholder = '',
  onSuggestionSelect,
  inputValue,
  setInputValue
}) => {
  const config = useContext(ConfigurationContext);
  const theme = getThemeColors(config.isDark);


  const [filteredSuggestions, setFilteredSuggestions] = useState<string[]>([]);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState<number>(-1);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setInputValue(value);
    setHighlightedIndex(-1); // Reset highlighted index

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
    setInputValue(suggestion);
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
    <div style={{
      position: 'relative',
      width: "100%"
    }}>
      <input
        type="text"
        value={inputValue}
        onChange={handleInputChange}
        onKeyDown={handleKeyDown}
        placeholder={placeholder}
        style={{
          width: '100%',
          padding: '6px',
          fontSize: '16px',
          borderRadius: '4px',
          border: `1px solid ${showSuggestions ? theme.primaryBorder : theme.simpleBorder}`,
          backgroundColor: "transparent",
          color: theme.generalText,
        }}
      />
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
            borderRadius: '4px',
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
                backgroundColor: highlightedIndex === index ? theme.highlightContainer : theme.generalContainer,
                color: highlightedIndex === index ? theme.highlightText : theme.generalText,
                borderBottom: `1px solid ${theme.secondaryBorder}`,
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = theme.highlightContainer
                e.currentTarget.style.color = theme.highlightText;
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = theme.generalContainer
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

export default InputWithSuggestions;
