import React, { createContext, ReactElement, useEffect, useState } from "react";
import cefMessage from "../cefMessages";
import { MessageType } from "../enums/MessageType";

export const RawRequestContext = createContext<string[]>([]);

export default function RawRequestProvider({
  children,
}: {
  children: ReactElement;
}) {
  let [rawReq, setRawReq] = useState<string[]>([]);

  useEffect(() => {
    cefMessage({
      type: MessageType.GetRawRequest,
      data: "",
      onSuccess: (data) => {
        if (data != null) {
          try {
            let parsedData = JSON.parse(data);
            setRawReq(parsedData);
          } catch (err) {
            console.log(
              "There was error parsing the data in rawRequest contxt",
            );
            console.log(err);
          }
        }
      },
    });
  }, []);

  return (
    <RawRequestContext.Provider value={rawReq}>
      {children}
    </RawRequestContext.Provider>
  );
}
