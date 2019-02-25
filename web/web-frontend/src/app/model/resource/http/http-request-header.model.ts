import {JsonUtil} from "../../../utils/json.util";
import {Serializable} from "../../infrastructure/serializable.model";

export class HttpRequestHeader implements Serializable<HttpRequestHeader> {
    key: string;
    value: string = "";

    deserialize(input: Object): HttpRequestHeader {
        throw new Error("this should not be used")
    }

    serialize(): string {
        if(!this.key && !this.value) {
            return "";
        }

        return ''+
            JsonUtil.stringify(this.key) + ':' + (this.value ? JsonUtil.stringify(this.value) : JsonUtil.stringify(""));
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
