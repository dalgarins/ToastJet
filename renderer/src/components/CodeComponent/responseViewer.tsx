// import React, { useContext } from "react";
// import { ConfigurationContext } from "../../context/configurationProvider";
// import { getThemeColors } from "../../themes/getThemeColors";
// import { ResponseContext } from "../../context/responseContext";
// import JsonXmlRenderer from "./JsonXmlRenderer";
// import HTMLIframeRenderer from "./HTMLIframeRenderer";
// import { ClientResponse } from "../../../common/interface";
// import { inspect } from "util-ex";

// export default function ResponseViewer({ res }: { res: ClientResponse | null }) {
//     const config = useContext(ConfigurationContext)
//     const themeStyle = getThemeColors(config.theme)
//     const response = res ?? useContext(ResponseContext).response;


//     if ((response.mime ?? (response.headers as any)["content-type"]).includes("image")) {
//         return <div style={{
//             overflowY: "scroll",
//         }}>
//             <img src={response.parsedUrl} />
//         </div>
//     }
//     if (response.mime.includes("html")) {
//         return <HTMLIframeRenderer
//             htmlContent={response.data}
//         />
//     }
//     if (response.mime.includes("json")) {
//         return <JsonXmlRenderer
//             type="json"
//             value={
//                 typeof response.data === "object"
//                     ? JSON.stringify(response.data, null, 2)
//                     : response.data
//             }
//             key={`json-${response.name}-json`}
//         />
//     }
//     if (response.mime.includes("xml")) {
//         return <JsonXmlRenderer type="xml" value={typeof response.data !== "string" ? inspect(response.data) : response.data} key={`xml-${response.name}-xml`} />
//     }
//     return <pre style={{
//         backgroundColor: "transparent",
//         borderRadius: '8px',
//         padding: '20px',
//         textAlign: 'center',
//         margin: '10px 0',
//         boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
//         color: themeStyle.generalText,
//     }}>
//         {!response.data ? (
//             <div style={{
//                 fontSize: '20px',
//                 fontWeight: 'bold',
//                 margin: '0 auto',
//                 maxWidth: '400px',
//             }}>
//                 <span style={{ fontSize: '30px' }}>❌</span>
//                 <div style={{ marginTop: '10px' }}>Oops! No response found.</div>
//                 <div style={{ marginTop: '5px', fontSize: '16px', color: '#888' }}>
//                     Check your request, or Server might also be down
//                 </div>
//                 <div style={{ marginTop: '5px' }}>Check error tab for more info </div>
//                 <br />
//                 <span style={{ fontSize: '26px' }}>🤷‍♂️🤷‍♀️</span>
//             </div>
//         ) : (
//             response.data
//         )}
//     </pre>


// }
