
import {JsonUtil} from "../../../utils/json.util";

export class HttpResponseHeader implements Serializable<HttpResponseHeader> {
    key: string;
    values: Array<string> = [];

    deserialize(input: Object): HttpResponseHeader {
        this.key = input["key"];
        this.values = input["values"];
        return this;
    }

    serialize(): string {
        if(this.isEmpty()) {
            return "";
        }

        return ''+
            '{' +
            '"key":' + JsonUtil.stringify(this.key) + ',' +
            '"value":' + JsonUtil.serializeArray(this.values) +
            '}';
    }

    isEmpty(): boolean {
        return !(this.key || this.values.length != 0);
    }
}
