const helperLib = `
declare function getRandomMail(): string;
declare function greet(name: string): string;
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
enum InputType {
  Request,
  Variable,
  Cookies,
  Environment,
};

enum TimeOutType {
  ms,
  s,
  mins,
};

enum FormDataItem {
  text,
  number,
  file,
  boolean,
  json,
  xml,
}

enum Https {
  "HTTP/1.1",
  "HTTP/2.0",
};
declare interface Headers {
  "Content-Type"?: string;
  "Authorization"?: string;
  "Accept"?: string;
  "credentials"?: "include" | "omit" | "same-origin";
}

let headers: Headers;
let method: HttpMethod | "GET" | "POST" | "PATCH" | "PUT" | "OPTIONS";
let url: string;
let json: any;

declare interface Cookie {
  key: string;
  value: string;
  expires: Date | 'Infinity' | null;
  maxAge: number | 'Infinity' | '-Infinity' | null;
  domain: string | null;
  path: string | null;
  secure: boolean;
  httpOnly: boolean;
  extensions: string[] | null;
  creation: Date | 'Infinity' | null;
  creationIndex: number;
  hostOnly: boolean | null;
  pathIsDefault: boolean | null;
  lastAccessed: Date | 'Infinity' | null;
  sameSite: string | undefined;
}

type GeneratorArgs = () => {
  error:boolean,
  message:string
};

declare function GenFunction(
  action: GeneratorArgs,
  option?: {
    global?: boolean;
  }
): GeneratorArgs;

type TestResponse = (
req:{
  url:string,
  headers:stirng,
  params:string,
  headers:Object,
  body:Object,
},
res: {
  error: boolean;
  mime: string;
  parsedUrl: string;
  timeTaken?: number;
  data: any;
  status: number;
  statusText: string;
  headers: Record<string, unknown>;
  size: number;
  cookie: Cookie[];
}) => boolean | string


// Updated type for TestFunction
declare function TestFunction(
  action: TestResponse,
  option?: {
    global?: boolean;
  }
): TestResponse;

`;

export { helperLib };
