import React, { useContext, useState } from 'react';
import { getThemeColors } from '../../themes/getThemeColors';
import { FaTrashAlt } from 'react-icons/fa';
import { ConfigurationContext } from '../../contexts/configurtionContext';
import fileHandler from '../../handler/files/fileHandler';

interface FileInputProps {
    placeholder?: string;
    flex: number;
}

const FileInputBox: React.FC<FileInputProps> = ({ placeholder, flex }) => {
    const [selectedFile, setSelectedFile] = useState<string | undefined | null>(null);
    const [isHovered, setIsHovered] = useState(false);
    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark);

    const handleFileChange = async (e: React.MouseEvent) => {
        let file = await fileHandler();
        setSelectedFile(file);
    };

    const removeFile = () => {
        setSelectedFile(null);
    };

    return (
        <div
            style={{
                position: 'relative',
                flexGrow: flex,
                display: 'flex',
                alignItems: 'center',
                minHeight: '32px',
                width: '100%', // Ensure full width
                background: theme.alternativeContainer
            }}
            onClick={handleFileChange}
        >
            <label
                htmlFor="file-upload"
                style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center', // Center text vertically and horizontally
                    height: "100%",
                    padding: '2px 6px', // Reduced padding
                    border: `1px solid ${isHovered ? theme.primaryBorder : 'transparent'}`,
                    borderRadius: '4px',
                    backgroundColor: theme.generalContainer,
                    color: theme.generalText,
                    cursor: 'pointer',
                    flexGrow: 1,
                    fontSize: '12px', // Reduced font size
                    position: 'relative',
                }}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
            >
                {selectedFile ? selectedFile.split("/").at(-1) : placeholder}
                {selectedFile && (
                    <FaTrashAlt
                        onClick={(e) => {
                            e.stopPropagation(); // Prevent label click
                            removeFile();
                        }}
                        style={{
                            position: 'absolute',
                            right: '8px',
                            color: isHovered ? 'red' : theme.generalText,
                            fontSize: '14px',
                            cursor: 'pointer',
                        }}
                        onMouseEnter={() => setIsHovered(true)}
                        onMouseLeave={() => setIsHovered(false)}
                    />
                )}
            </label>
        </div>
    );
};

export default FileInputBox;