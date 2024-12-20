import { RequestDataType } from "../VariableEnum";

enum HttpMethod {
    GET = 0,
    POST = 1,
    PUT = 2,
    PATCH = 3,
    DELETE = 4,
    HEAD = 5,
    OPTIONS = 6,
    CONNECT = 7,
    TRACE = 8,
    PURGE = 9,
    LINK = 10,
    UNLINK = 11,
    PROPFIND = 12,
    PROPPATCH = 13,
    MKCOL = 14,
    COPY = 15,
    MOVE = 16,
    LOCK = 17,
    UNLOCK = 18,
    REPORT = 19,
    VIEW = 20
};

export function getRequestTypeString(a: RequestDataType): string {
    switch (a) {
        case RequestDataType.formData:
            return "Form Data";
        case RequestDataType.rawJson:
            return "Raw JSON";
        case RequestDataType.rawJs:
            return "Raw JavaScript";
        case RequestDataType.rawXml:
            return "Raw XML";
        case RequestDataType.rawHtml:
            return "Raw HTML";
        case RequestDataType.rawText:
            return "Raw Text";
        case RequestDataType.urlEncoded:
            return "URL Encoded";
        case RequestDataType.none:
            return "None";
        case RequestDataType.binary:
            return "Binary Data";
        default:
            return "None";
    }
}

enum Https {
    "HTTP/1.1",
    "HTTP/2.0",
}

const Methods = {
    GET: {
        label: "GET",
        method: HttpMethod.GET,
        colors: ["#e0f7fa", "#00796b", "#22ff40", "#b2dfdb"], // Light, Dark, Dark-Contrast, Light-Contrast
    },
    POST: {
        label: "POST",
        method: HttpMethod.POST,
        colors: ["#ffe0b2", "#ff6f00", "#ff8800", "#ffcc80"], // More vibrant orange
    },
    PUT: {
        label: "PUT",
        method: HttpMethod.PUT,
        colors: ["#c8e6c9", "#388e3c", "#aaeefe", "#a5d6a7"], // Green shades
    },
    PATCH: {
        label: "PATCH",
        method: HttpMethod.PATCH,
        colors: ["#421233", "#fbc02d", "#f57f17", "#fff59d"], // Yellow shades
    },
    DELETE: {
        label: "DELETE",
        method: HttpMethod.DELETE,
        colors: ["#ffcdd2", "#d32f2f", "#b71c1c", "#ef9a9a"], // Red shades
    },
    HEAD: {
        label: "HEAD",
        method: HttpMethod.HEAD,
        colors: ["#bbdefb", "#1976d2", "#0d47ff", "#90caf9"], // Blue shades
    },
    OPTIONS: {
        label: "OPTIONS",
        method: HttpMethod.OPTIONS,
        colors: ["#d1c4e9", "#673ab7", "#f11b92", "#b39ddb"], // Purple shades
    },
    CONNECT: {
        label: "CONNECT",
        method: HttpMethod.CONNECT,
        colors: ["#cfd8dc", "#455a64", "#fdfe01", "#b0bec5"], // Grey shades
    },
    TRACE: {
        label: "TRACE",
        method: HttpMethod.TRACE,
        colors: ["#f8bbd0", "#d81b60", "#880e4f", "#f48fb1"], // Pink shades
    },
    PURGE: {
        label: "PURGE",
        method: HttpMethod.PURGE,
        colors: ["#ffe0b2", "#ff9800", "#e65100", "#ffcc80"], // Orange shades
    },
    LINK: {
        label: "LINK",
        method: HttpMethod.LINK,
        colors: ["#c8e6c9", "#388e3c", "#1b5e20", "#a5d6a7"], // Green shades
    },
    UNLINK: {
        label: "UNLINK",
        method: HttpMethod.UNLINK,
        colors: ["#bbdefb", "#2196f3", "#af47a1", "#90caf9"], // Blue shades
    },
    PROPFIND: {
        label: "PROPFIND",
        method: HttpMethod.PROPFIND,
        colors: ["#d1c4e9", "#673ab7", "#715bc2", "#b39ddb"], // Purple shades
    },
    PROPPATCH: {
        label: "PROPPATCH",
        method: HttpMethod.PROPPATCH,
        colors: ["#ffcdd2", "#f44336", "#b71c1c", "#ef9a9a"], // Red shades
    },
    MKCOL: {
        label: "MKCOL",
        method: HttpMethod.MKCOL,
        colors: ["#ffe0b2", "#ff9800", "#e65100", "#ffcc80"], // Orange shades
    },
    COPY: {
        label: "COPY",
        method: HttpMethod.COPY,
        colors: ["#c8e6c9", "#388e3c", "#1bfe20", "#a5d6a7"], // Green shades
    },
    MOVE: {
        label: "MOVE",
        method: HttpMethod.MOVE,
        colors: ["#5f4411", "#fbc02d", "#f57f17", "#fff59d"], // Yellow shades
    },
    LOCK: {
        label: "LOCK",
        method: HttpMethod.LOCK,
        colors: ["#bbdefb", "#2196f3", "#0047df", "#90caf9"], // Blue shades
    },
    UNLOCK: {
        label: "UNLOCK",
        method: HttpMethod.UNLOCK,
        colors: ["#d1c4e9", "#673ab7", "#b11b92", "#b39ddb"], // Purple shades
    },
    REPORT: {
        label: "REPORT",
        method: HttpMethod.REPORT,
        colors: ["#ffcdd2", "#f44336", "#b71c1c", "#ef9a9a"] // Red shades
    },
    VIEW: {
        label: "VIEW",
        method: HttpMethod.VIEW,
        colors: ["#ffe0b2", "#ff9800", "#e65100", "#ffcc80"] // Orange shades
    }
};

export enum ResponseType {
    json = "json",
    text = "text",
    blob = "blob",
    arrayBuffer = "arrayBuffer",
    formdata = "formdata",
    document = "document",
    stream = "stream",
}

export { Https, HttpMethod, Methods };