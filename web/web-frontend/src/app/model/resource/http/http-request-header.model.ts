
import {JsonUtil} from "../../../utils/json.util";

export class HttpRequestHeader implements Serializable<HttpRequestHeader> {
    key: string;
    value: string;

    deserialize(input: Object): HttpRequestHeader {
        this.key = input["key"];
        this.value = input["value"];
        return this;
    }

    serialize(): string {
        if(!this.key && !this.value) {
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
