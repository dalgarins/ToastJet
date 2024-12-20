import { HttpMethod, Https } from "../enums/Apis/method";
import { FormDataItem, RequestDataType, TimeOutType } from "../enums/VariableEnum";
import { FormDataType, KeyValueCheckRecord } from "../interfaces/variableData";

export default function requestExtractor(a: string) {
    try {
        const func = new Function(`
            with (this) {
                let name, method, url, form, local, project, requestDataType, https, headers, params, path, timeout, 
                timeoutType, js, html, text, formData, binary, urlEncoded, json, xml, requestCookies, nonce, examples;
        
                ${a}
                return {name, method, nonce, url, form, binary, local, project, requestDataType, https, headers, params, 
                path, timeout, timeoutType, js, html, text, formData, urlEncoded, json, xml, requestCookies, examples};
            }
        `);

        let exData = func.call({
            HttpMethod,
            TimeOutType,
            Https,
            RequestDataType,
        });
        if (exData.headers) {
            exData.headers = parseHeaderParamsEncoded(exData.headers);
        }
        if (exData.params) {
            exData.params = parseHeaderParamsEncoded(exData.params);
        }
        if (exData.formData) {
            exData.formData = parseFormData(exData.formData);
        }
        if (exData.urlEncoded) {
            exData.urlEncoded = parseHeaderParamsEncoded(exData.urlEncoded);
        }
        return exData;
    } catch (err) {
        console.error("Eval failed:", err);
    }
}

export function parseHeaderParamsEncoded(encoded: any): KeyValueCheckRecord[] {
    const result: KeyValueCheckRecord[] = [];

    for (const key in encoded) {
        const value = encoded[key];

        if (typeof value === "object" && value.enabled !== undefined) {
            result.push({
                key,
                value: value.value,
                enabled: value.enabled,
            });
        } else {
            result.push({
                key,
                value,
                enabled: true,
            });
        }
    }

    return result;
}

export function parseFormData(encoded: any): FormDataType[] {
    const result: FormDataType[] = [];

    for (const key in encoded) {
        const value = encoded[key];

        if (typeof value === "object" && value.type !== undefined) {
            result.push({
                key,
                value: value.value,
                type: value.type,
                enabled: value.enabled !== undefined ? value.enabled : true,
            });
        } else {
            result.push({
                key,
                value,
                type: FormDataItem.text, // Assuming default type if not provided
                enabled: true,
            });
        }
    }

    return result;
}

