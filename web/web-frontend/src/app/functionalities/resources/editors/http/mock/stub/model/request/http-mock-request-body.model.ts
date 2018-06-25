
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {HttpMockRequestBodyMatchingType} from "../enums/http-mock-request-body-matching-type.enum";
import {HttpMockRequestBodyVerifyType} from "../enums/http-mock-request-body-verify-type.enum";

export class HttpMockRequestBody implements Serializable<HttpMockRequestBody> {

    matchingType: HttpMockRequestBodyMatchingType;
    bodyType: HttpMockRequestBodyVerifyType = HttpMockRequestBodyVerifyType.TEXT;
    content: string;

    constructor() {
        this.reset()
    }

    reset(): void {
        this.matchingType = undefined;
        this.bodyType = HttpMockRequestBodyVerifyType.TEXT;
        this.content = undefined;
    }

    isEmpty(): boolean {
        return this.content == null
    }

    deserialize(input: Object): HttpMockRequestBody {
        if (input['matchingType']) {
            this.matchingType = HttpMockRequestBodyMatchingType.fromSerialization(input['matchingType']);
        }

        if (input['bodyType']) {
            this.bodyType = HttpMockRequestBodyVerifyType.fromSerialization(input['bodyType']);
        }

        if (input['content']) {
            this.content = input['content'];
        }

        return this;
    }

    serialize(): string {
        let shouldAddComa = false;

        let result = '{';
        if (this.matchingType) {
            shouldAddComa = true;
            result += '"matchingType":' + JsonUtil.stringify(this.matchingType.asSerialized);
        }

        if (this.bodyType) {
            if(shouldAddComa) {
                result += ',';
            }
            shouldAddComa = true;
            result += '"bodyType":' + JsonUtil.stringify(this.bodyType.asSerialized);
        }

        if (this.content) {
            if(shouldAddComa) {
                result += ',';
            }
            shouldAddComa = true;

            if(this.matchingType == HttpMockRequestBodyMatchingType.JSON_VERIFY) {
                result += '"content":' + JsonUtil.stringify(this.content);
            } else {
                result += '"content":' + JsonUtil.stringify(this.content);
            }
        }

        result += '}';

        return result;
    }

}
