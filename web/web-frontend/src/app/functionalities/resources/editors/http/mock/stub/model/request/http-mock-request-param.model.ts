
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {HttpMockRequestParamsCompareMode} from "../enums/http-mock-request-params-compare-mode.enum";

export class HttpMockRequestParam implements Serializable<HttpMockRequestParam> {
    key: string;
    compareMode: HttpMockRequestParamsCompareMode = HttpMockRequestParamsCompareMode.EXACT_MATCH;
    value: string;

    deserialize(input: Object): HttpMockRequestParam {
        this.key = input["key"];
        this.compareMode = HttpMockRequestParamsCompareMode.fromSerialization(input["compareMode"]);
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
        if (this.compareMode != HttpMockRequestParamsCompareMode.ABSENT) {
            response += ',"value":' + JsonUtil.stringify(this.value);
        }
        response += '}';
        return response;
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
