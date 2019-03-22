
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";

export class HttpMockResponseHeader implements Serializable<HttpMockResponseHeader> {
    key: string;
    value: string;

    deserialize(input: Object): HttpMockResponseHeader {
        throw new Error("this should not be used")
    }

    serialize(): string {
        if(!this.key && !this.value) {
            return "";
        }

        let serializedHeaderKey = this.key ? JsonUtil.stringify(this.key): JsonUtil.stringify("");
        return ''+
            serializedHeaderKey + ':' + JsonUtil.stringify(this.value);
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
