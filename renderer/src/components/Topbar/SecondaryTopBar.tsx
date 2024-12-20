import React, { useContext, useState } from 'react';
import { getThemeColors } from '../../themes/getThemeColors';
import { ConfigurationContext } from '../../contexts/configurtionContext';

type Item = {
    name: string;
    msg: number;
};

type SecondaryTopBarProps = {
    items: Item[];
    selectedIndex: number;
    onSelect: (index: number) => void;
};

const SecondaryTopBar: React.FC<SecondaryTopBarProps> = ({ items, selectedIndex, onSelect }) => {
    const [showBreadcrumb, setShowBreadcrumb] = useState(false);
    const config = useContext(ConfigurationContext)
    const theme = getThemeColors(config.isDark);
    const handleItemClick = (index: number) => {
        onSelect(index);
        setShowBreadcrumb(false);
    };

    const handleResize = () => {
        const container = document.getElementById('topbar-container');
        if (container) {
            setShowBreadcrumb(container.scrollWidth > container.clientWidth);
        }
    };

    React.useEffect(() => {
        handleResize();
        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, [items]);

    return (
        <div style={{ position: 'relative' }}>
            <div id="topbar-container" style={{ display: 'flex', overflowX: 'auto' }}>
                {items.map((item, index) => (
                    <div
                        key={index}
                        onClick={() => handleItemClick(index)}
                        style={{
                            padding: '8px 10px',
                            cursor: 'pointer',
                            color: selectedIndex === index ? theme.primaryContainer : theme.generalText,
                            borderBottom: selectedIndex === index ? `2px solid ${theme.primaryContainer}` : 'none',
                        }}
                    >
                        {item.name} <span style={{
                            color:theme.accentColor
                        }}> {item.msg > 0 && `(${item.msg})`}</span>
                    </div>
                ))}
            </div>
            {showBreadcrumb && (
                <div style={{ marginTop: '10px', color: theme.generalText }}>
                    <span>...</span>
                </div>
            )}
        </div>
    );
};

export default SecondaryTopBar;