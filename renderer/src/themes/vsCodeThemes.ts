export const vsCodeThemes: Record<number, any> = [
  {
    base: "vs",
    inherit: true,
    rules: [
      { token: "comment", foreground: "#808080", fontStyle: "italic" }, // Gray comment
      { token: "keyword", foreground: "#0000ff" }, // Blue keyword
      { token: "string", foreground: "#008000" }, // Green string
      { token: "variable", foreground: "#01ffab" }, // Light cyan variable
    ],
    colors: {
      "editor.background": "#00000000", // Transparent background
      "editor.lineHighlightBackground": "#f0f0f0", // Light gray highlight
      "editorCursor.foreground": "#000000", // Black cursor
    },
  },
  {
    base: "vs-dark",
    inherit: true,
    rules: [
      { token: "comment", foreground: "#ffa500", fontStyle: "italic" }, // Orange comment
      { token: "keyword", foreground: "#ff007f" }, // Pink keyword
      { token: "string", foreground: "#00ff00" }, // Bright green string
      { token: "variable", foreground: "#01ffab" }, // Light cyan variable
    ],
    colors: {
      "editor.background": "#00000000", // Transparent background
      "editor.lineHighlightBackground": "#333333", // Dark gray highlight
      "editorCursor.foreground": "#ffffff", // White cursor
    },
  },
];

export function getVsCodeTheme(isDark: boolean) {
  return isDark ? vsCodeThemes[0] : vsCodeThemes[1];
}

export default getVsCodeTheme;
