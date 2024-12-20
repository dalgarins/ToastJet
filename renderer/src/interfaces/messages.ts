import { MessageType } from "../enums/MessageType";

export interface MessageData {
  type: MessageType;
  data: any;
}
