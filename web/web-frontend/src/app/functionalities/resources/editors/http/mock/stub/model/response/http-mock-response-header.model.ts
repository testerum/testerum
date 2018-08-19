
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";

export class HttpMockResponseHeader implements Serializable<HttpMockResponseHeader> {
    key: string;
    value: string;

    deserialize(input: Object): HttpMockResponseHeader {
        this.key = input["key"];
        this.value = input["value"];
        return this;
    }

    serialize(): string {
        if(this.isEmpty()) {
            return "";
        }

        return ''+
            '{' +
            '"key":' + JsonUtil.stringify(this.key) + ',' +
            '"value":' + JsonUtil.stringify(this.value) +
            '}';
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
