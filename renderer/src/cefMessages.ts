import { MessageType } from "./enums/MessageType";

export default function cefMessage({
  type,
  data,
  onSuccess,
  onFailure,
}: {
  type: MessageType;
  data: any;
  onSuccess: (x: string | null) => void;
  onFailure?: (x: string | null) => void;
}) {
  //@ts-ignore
  window.cefQuery({
    request: JSON.stringify({
      type,
      data: data ?? "",
    }),
    onSuccess,
    onFailure: onFailure ?? ((a: string) => {}),
  });
}
