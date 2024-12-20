"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.MessageEngine = exports.MessageClient = void 0;
var MessageClient;
(function (MessageClient) {
    MessageClient[MessageClient["Initialize"] = 0] = "Initialize";
    MessageClient[MessageClient["ChangeTheme"] = 1] = "ChangeTheme";
})(MessageClient || (exports.MessageClient = MessageClient = {}));
var MessageEngine;
(function (MessageEngine) {
    MessageEngine[MessageEngine["GetResponse"] = 0] = "GetResponse";
    MessageEngine[MessageEngine["GenerateRequest"] = 1] = "GenerateRequest";
})(MessageEngine || (exports.MessageEngine = MessageEngine = {}));
//# sourceMappingURL=message.js.map