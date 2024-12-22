import React, { useContext, useEffect, useRef, useState } from "react";
import { getThemeColors, ThemeColors } from "../../themes/getThemeColors";
import { ConfigurationContext } from "../../contexts/configurtionContext";

export interface Option {
  label: string;
  value: string | number | boolean;
  color?: string;
}

interface CustomSelectProps {
  options: Option[];
  value: number;
  onChange: (option: number) => void;
  styles?: any;
  theme: ThemeColors;
}

const CustomSelect: React.FC<CustomSelectProps> = ({
  options,
  value,
  onChange,
  styles,
}) => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [highlightedIndex, setHighlightedIndex] = useState(0);
  const configuration = useContext(ConfigurationContext);
  const theme = getThemeColors(configuration.isDark);

  const dropdownRef = useRef<HTMLDivElement | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);

  const toggleDropdown = () => {
    setIsOpen(!isOpen);
  };

  const handleOptionClick = (option: number) => {
    onChange(option);
    setIsOpen(false);
  };

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchTerm(e.target.value);
    setHighlightedIndex(0);
  };

  const filteredOptions = options.filter((option) =>
    option.label.toLowerCase().includes(searchTerm.toLowerCase()),
  );

  const handleKeyDown = (e: React.KeyboardEvent<HTMLDivElement>) => {
    if (!isOpen) return;

    if (e.key === "ArrowDown") {
      setHighlightedIndex((prevIndex) =>
        prevIndex < filteredOptions.length - 1 ? prevIndex + 1 : 0,
      );
    } else if (e.key === "ArrowUp") {
      setHighlightedIndex((prevIndex) =>
        prevIndex > 0 ? prevIndex - 1 : filteredOptions.length - 1,
      );
    } else if (e.key === "Enter") {
      if (filteredOptions[highlightedIndex]) {
        handleOptionClick(highlightedIndex);
      }
    } else if (e.key === "Escape") {
      setIsOpen(false);
    }
  };

  const handleBlur = (e: React.FocusEvent<HTMLDivElement>) => {
    if (
      dropdownRef.current &&
      !dropdownRef.current.contains(e.relatedTarget as Node)
    ) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    if (menuRef.current && isOpen) {
      const menuElement = menuRef.current;
      const highlightedOption = menuElement.children[
        highlightedIndex
      ] as HTMLElement;
      if (highlightedOption) {
        highlightedOption.scrollIntoView({ block: "nearest" });
      }
    }
  }, [highlightedIndex, isOpen]);

  useEffect(() => {
    if (!isOpen) {
      setHighlightedIndex(0);
    }
  }, [isOpen]);

  return (
    <div
      ref={dropdownRef}
      style={{
        position: "relative",
        width: "100%",
        ...styles?.container,
        border: `1px solid ${theme.simpleBorder}`,
        borderRadius: "4px",
      }}
      tabIndex={0}
      onKeyDown={handleKeyDown}
      onBlur={handleBlur}
    >
      <div
        onClick={toggleDropdown}
        style={{
          display: "flex",
          alignItems: "center",
          padding: "4px",
          borderRadius: "4px",
          // backgroundColor: theme.alternativeContainer,
          cursor: "pointer",
          ...styles?.control,
          color: options[value]?.color ?? theme.generalText,
        }}
      >
        {value !== undefined ? options[value]?.label : "Select an option"}
        <span
          style={{
            marginLeft: "auto",
            marginRight: "5px",
            color: theme.primaryContainer,
            transform: isOpen ? "rotate(0deg)" : "rotate(180deg)",
            transition: "transform 0.7s ease",
          }}
        >
          ^
        </span>
      </div>
      {isOpen && (
        <div
          ref={menuRef}
          style={{
            position: "absolute",
            top: "100%",
            left: 0,
            right: 0,
            zIndex: 100,
            // background: theme.alternativeContainer,
            border: `1px solid ${styles?.borderColor || theme.primaryBorder || "#ccc"}`,
            borderRadius: "4px",
            maxHeight: "200px",
            overflowY: "auto",
            overflowX: "hidden",
            scrollBehavior: "smooth", // Ensure smooth scrolling
            ...styles?.menu,
          }}
          onWheel={(e) => e.stopPropagation()} // Prevent default scrolling behavior
        >
          <input
            type="text"
            value={searchTerm}
            onChange={handleSearchChange}
            placeholder="Search..."
            style={{
              width: "100%",
              padding: "3px",
              border: "none",
              borderBottom: `1px solid ${styles?.borderColor || theme.primaryBorder || "#ccc"}`,
              color: theme.generalText,
              height: "20px",
              paddingLeft: "4px",
              background: theme.alternativeContainer,
              ...styles?.searchInput,
            }}
          />
          {filteredOptions.map((option, index) => (
            <div
              key={option.value.toString()}
              onClick={() => handleOptionClick(index)}
              style={{
                padding: "2px",
                cursor: "pointer",
                backgroundColor:
                  highlightedIndex === index
                    ? theme.highlightContainer
                    : theme.generalContainer,
                color:
                  highlightedIndex === index // Use option's color for highlighted text
                    ? theme.highlightText
                    : (option?.color ?? theme.generalText),
                ...styles?.option,
              }}
              onMouseEnter={() => setHighlightedIndex(index)}
              onMouseLeave={() => setHighlightedIndex(-1)}
              ref={
                highlightedIndex === index
                  ? (el) =>
                      el &&
                      el.scrollIntoView({
                        behavior: "smooth",
                        block: "nearest",
                      })
                  : null
              } // Smoothly scroll to highlighted option
            >
              {option.label}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default CustomSelect;
