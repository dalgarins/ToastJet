import { FormDataItem, VariableDataType } from "../enums/VariableEnum";

export interface VariableInfo {
    key: string;
    value: string;
    enabled: boolean;
    type: VariableDataType
}

export interface EnvironmentInfo {
    path: string,
    enabled: boolean,
    status: boolean,
}

export type TestFunction = (req: Object, res: Object) => boolean

export interface FunctionProps {
    name: string;
    example?: string;
    description?: string;
    params?: Record<string, string>;
}

export interface FunctionInfo extends FunctionProps {
    fn: Function
}

export interface KeyValueCheckRecord {
    key: string,
    value: string,
    enabled: boolean,
}

export interface FormDataType {
    key: string,
    value: string,
    enabled: boolean,
    type: FormDataItem
}


export interface Option {
    label: string;
    value: string | number | boolean;
    color?: string; 
}