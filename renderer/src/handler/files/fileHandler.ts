import cefMessage from "../../cefMessages";
import { MessageType } from "../../enums/MessageType";

export default async function fileHandler() {
    return new Promise<string | null>((resolve, reject) => {
        cefMessage({
            type: MessageType.FilePicker,
            data: "",
            onSuccess: (data) => {
                resolve(data);
            },
            onFailure: (error) => {
                resolve(null);
            },
        });
    })
}