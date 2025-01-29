export type ThemeColors = {
  generalContainer: string;
  generalText: string;
  alternativeContainer: string;
  alternativeText: string;
  primaryContainer: string;
  primaryText: string;
  secondaryContainer: string;
  secondaryText: string;
  tertiaryContainer: string;
  tertiaryText: string;
  errorContainer: string;
  errorText: string;
  warningContainer: string;
  warningText: string;
  highlightContainer: string;
  highlightText: string;
  hoverContainer: string;
  hoverText: string;
  primaryBorder: string;
  secondaryBorder: string;
  accentColor: string;
  infoColor: string;
  infoContainer: string;
  disabledColor: string;
  disabledContainer: string;
  successContainer: string;
  successText: string;
  simpleBorder: string;
};

export const themes: Record<number, ThemeColors> = [
  {
    generalContainer: "#FFFFFF",
    generalText: "#212529",
    alternativeContainer: "#E8F5E9",
    alternativeText: "#004d00",
    primaryContainer: "#4CAF50", // Green
    primaryText: "#FFFFFF",
    secondaryContainer: "#C8E6C9",
    secondaryText: "#FFFFFF",
    tertiaryContainer: "#F8F9FA",
    tertiaryText: "#495057",
    errorText: "#FFCDD2",
    errorContainer: "#C62828",
    warningContainer: "#FFF3CD",
    warningText: "#856404",
    highlightContainer: "#A5D6A7",
    highlightText: "#1B5E20",
    hoverContainer: "#E0E0E0",
    hoverText: "#212529",
    primaryBorder: "#388E3C", // Darker green border
    secondaryBorder: "#4CAF50", // Green border for secondary elements
    simpleBorder: "#B0BEC5",
    accentColor: "#FFC107", // Amber accent color
    infoColor: "#81C784", // Light green info color
    infoContainer: "#E8F5E9", // Pale green background for info messages
    disabledColor: "#A6A6A6",
    disabledContainer: "#F8F9FA",
    successContainer: "#D1E7DD",
    successText: "#155724",
  },
  {
    generalContainer: "#1F1F1F",
    generalText: "#EAEAEA",
    alternativeContainer: "#2A2A2A",
    alternativeText: "#FFFFFF",
    primaryContainer: "#4CAF50", // Green
    primaryText: "#FFFFFF",
    secondaryContainer: "#4B4B4B",
    secondaryText: "#EAEAEA",
    tertiaryContainer: "#3C3C3C",
    tertiaryText: "#B0BEC5",
    errorText: "#FFCDD2",
    errorContainer: "#C62828",
    warningContainer: "#F78",
    warningText: "#FFFFFF",
    highlightContainer: "#388E3C",
    highlightText: "#FFFFFF",
    hoverContainer: "#66BB6A",
    hoverText: "#002208",
    primaryBorder: "#4CAF50",
    secondaryBorder: "#81C784",
    simpleBorder: "#616161",
    accentColor: "#81C784",
    infoColor: "#29B6F6",
    infoContainer: "#E1F5FE",
    disabledColor: "#757575",
    disabledContainer: "#424242",
    successText: "#C8E6C9",
    successContainer: "#388E3C",
  },
];

export function getThemeColors(isDark: boolean): ThemeColors {
  return isDark ? themes[1] : themes[0];
}

export default themes;
