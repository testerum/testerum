
import {JsonUtil} from "../../../../../../utils/json.util";
import {HttpResponseVerifyHeadersCompareMode} from "./enums/http-response-verify-headers-compare-mode.enum";

export class HttpResponseHeaderVerify implements Serializable<HttpResponseHeaderVerify> {
    key: string;
    compareMode: HttpResponseVerifyHeadersCompareMode = HttpResponseVerifyHeadersCompareMode.EXACT_MATCH;
    value: string;

    deserialize(input: Object): HttpResponseHeaderVerify {
        this.key = input["key"];
        this.compareMode = HttpResponseVerifyHeadersCompareMode.fromSerialization(input["compareMode"]);
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
            '"compareMode":' + JsonUtil.stringify(this.compareMode.asSerialized) + ',' +
            '"value":' + JsonUtil.stringify(this.value) +
            '}';
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
