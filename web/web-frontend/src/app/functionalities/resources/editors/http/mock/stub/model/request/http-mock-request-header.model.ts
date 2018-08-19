
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {HttpMockRequestHeadersCompareMode} from "../enums/http-mock-request-headers-compare-mode.enum";
import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";

export class HttpMockRequestHeader implements Serializable<HttpMockRequestHeader> {
    key: string;
    compareMode: HttpMockRequestHeadersCompareMode = HttpMockRequestHeadersCompareMode.EXACT_MATCH;
    value: string;

    deserialize(input: Object): HttpMockRequestHeader {
        this.key = input["key"];
        this.compareMode = HttpMockRequestHeadersCompareMode.fromSerialization(input["compareMode"]);
        this.value = input["value"];
        return this;
    }

    serialize(): string {
        if(!this.key && !this.value) {
            return "";
        }

        let response = '{';
        response += '"key":' + JsonUtil.stringify(this.key);
        response += ',"compareMode":' + JsonUtil.stringify(this.compareMode.asSerialized);
        if (this.compareMode != HttpMockRequestHeadersCompareMode.ABSENT) {
            response += ',"value":' + JsonUtil.stringify(this.value);
        }
        response += '}';
        return response;
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
