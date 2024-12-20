import React, { ReactElement, ReactNode, useContext, useState } from 'react';
import { DndContext, DragEndEvent } from '@dnd-kit/core';
import { SortableContext, useSortable, verticalListSortingStrategy } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { getThemeColors } from '../../themes/getThemeColors';
import { ConfigurationContext } from '../../contexts/configurtionContext';

interface DraggableListProps {
    children: ReactElement[];
    onDragEnd: (result: any) => void;
    header: ReactElement;
}

const DraggableItem = ({ id, children, border }: { id: string, children: ReactElement, border: boolean }) => {
    const { attributes, listeners, setNodeRef, transform } = useSortable({ id });
    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark);
    const style = {
        transform: CSS.Translate.toString(transform),
        display: 'flex',
        width: '100%',
        alignItems: 'center',
        borderBottom: border ? `0.5px solid ${theme.simpleBorder}` : 'none',
        transition: 'background-color 0.3s ease',
    };

    return (
        <div style={style} >
            <div style={{
                display: "flex",
                flexDirection: "column",
                justifyContent: "start",
                height: "100%",
                alignSelf: "start",
            }}>
                <div
                    ref={setNodeRef}
                    style={{
                        cursor: 'grab',
                        backgroundColor: 'transparent',
                        display: 'flex',
                        flexDirection: 'column',
                        justifyContent: 'center',
                        alignItems: 'center',
                        margin: '0 4px',
                        fontWeight: 'bold',
                        height: "30px",
                        flexShrink: 1,
                        flexGrow: 0,
                    }} {...listeners} {...attributes}>⋮⋮</div>
                <div style={{
                    height: "1px",
                    flexGrow: 1,
                }}></div>
            </div>
            <div style={{
                flexGrow: 1
            }}>{children}</div>
        </div>
    );
};

const DraggableList: React.FC<DraggableListProps> = ({ children, onDragEnd, header }) => {
    const items = React.Children.toArray(children).map((child, index) => ({
        id: `item-${index}`,
        content: child,
    }));

    const config = useContext(ConfigurationContext);
    const theme = getThemeColors(config.isDark);

    const handleDragEnd = (event: DragEndEvent) => {
        if (event.active.id !== event.over?.id) {
            onDragEnd(event);
        }
    };

    return (
        <DndContext onDragEnd={handleDragEnd}>
            {header && (
                <div style={{
                    backgroundColor: theme.tertiaryContainer,
                    display: 'flex',
                    width: '100%',
                    alignItems: 'center',
                    borderBottom: `0.5px solid ${theme.simpleBorder}`,
                    transition: 'background-color 0.3s ease',
                }}>
                    <div style={{
                        cursor: 'grab',
                        // padding: '4px',
                        backgroundColor: 'transparent',
                        display: 'inline-block',
                        margin: '0 4px',
                        fontWeight: 'bold',
                    }}>
                        ⋮⋮
                    </div>
                    <div style={{ flex: 1 }}>{header}</div>
                </div>
            )}
            <SortableContext items={items.map(item => item.id)} strategy={verticalListSortingStrategy}>
                <div style={{ backgroundColor: 'transparent', width: '100%' }}>
                    {items.map((item, i) => (
                        <DraggableItem key={item.id} id={item.id} border={i !== items.length - 1}>
                            {item.content as ReactElement}
                        </DraggableItem>
                    ))}
                </div>
            </SortableContext>

        </DndContext>
    );
};

export default DraggableList;