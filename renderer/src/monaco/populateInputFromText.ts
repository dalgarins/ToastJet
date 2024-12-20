import React from "react";
import requestExtractor from "./requestExtractor";
import { ApiData } from "../interfaces/apiData";

export default async function populateInputFormData(
    data: ApiData,
    setData: React.Dispatch<ApiData & { name: string }>
) {
    try {
        let extractedData = requestExtractor(data.rawCode);
        if (extractedData) {
            setData({
                ...data,
                ...(extractedData.name !== undefined && { name: extractedData.name }),
                ...(extractedData.method !== undefined && { method: extractedData.method }),
                ...(extractedData.url !== undefined && { url: extractedData.url }),
                ...(extractedData.https !== undefined && { https: extractedData.https }),
                ...(extractedData.params !== undefined && { params: extractedData.params }),
                ...(extractedData.headers !== undefined && { headers: extractedData.headers }),
                ...(extractedData.path !== undefined && { path: extractedData.path }),
                ...(extractedData.binary !== undefined && { binary: extractedData.binary }),
                ...(extractedData.formData !== undefined && { formData: extractedData.formData }),
                ...(extractedData.xml !== undefined && { xml: extractedData.xml }),
                ...(extractedData.urlEncoded !== undefined && { urlEncoded: extractedData.urlEncoded }),
                ...(extractedData.json !== undefined && { json: extractedData.json }),
                ...(extractedData.js !== undefined && { js: extractedData.js }),
                ...(extractedData.html !== undefined && { html: extractedData.html }),
                ...(extractedData.text !== undefined && { text: extractedData.text }),
                ...(extractedData.requestCookies !== undefined && { requestCookies: extractedData.requestCookies }),
                ...(extractedData.timeout !== undefined && { timeout: extractedData.timeout }),
                ...(extractedData.timeoutType !== undefined && { timeoutType: extractedData.timeoutType }),
                ...(extractedData.requestDataType !== undefined && { requestDataType: extractedData.requestDataType }),
                ...(extractedData.examples !== undefined && { examples: extractedData.examples }),
                nonce: extractedData.nonce ?? data.nonce
            });
        }
    } catch (err) {

    }
}