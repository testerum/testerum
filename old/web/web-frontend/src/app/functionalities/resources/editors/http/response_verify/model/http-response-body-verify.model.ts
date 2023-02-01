import {HttpBodyVerifyMatchingType} from "./enums/http-body-verify-matching-type.enum";
import {HttpBodyVerifyType} from "./enums/http-body-verify-type.enum";
import {JsonUtil} from "../../../../../../utils/json.util";
import {Serializable} from "../../../../../../model/infrastructure/serializable.model";

export class HttpResponseBodyVerify implements Serializable<HttpResponseBodyVerify> {

    private _httpBodyVerifyMatchingType: HttpBodyVerifyMatchingType;
    get httpBodyVerifyMatchingType(): HttpBodyVerifyMatchingType {
        return this._httpBodyVerifyMatchingType;
    }
    set httpBodyVerifyMatchingType(value: HttpBodyVerifyMatchingType) {
        this._httpBodyVerifyMatchingType = value;

        if (value == HttpBodyVerifyMatchingType.JSON_VERIFY) {
            this.httpBodyType = HttpBodyVerifyType.JSON;
        }
    }

    httpBodyType: HttpBodyVerifyType = HttpBodyVerifyType.TEXT;
    bodyVerify: string;

    constructor() {
        this.reset();
    }

    reset(): void {
        this.httpBodyVerifyMatchingType = undefined;
        this.httpBodyType = HttpBodyVerifyType.TEXT;
        this.bodyVerify = undefined;
    }

    deserialize(input: Object): HttpResponseBodyVerify {
        if (input['httpBodyType']) {
            this.httpBodyType = HttpBodyVerifyType.fromSerialization(input['httpBodyType']);
        }

        if (input['httpBodyVerifyMatchingType']) {
            this.httpBodyVerifyMatchingType = HttpBodyVerifyMatchingType.fromSerialization(input['httpBodyVerifyMatchingType']);
        }

        if (input['bodyVerify']) {
            this.bodyVerify = input['bodyVerify'];
        }

        return this;
    }

    serialize(): string {
        if (!this.httpBodyVerifyMatchingType) {
            return "";
        }

        let result = '{';
            result += '"httpBodyVerifyMatchingType":' + JsonUtil.stringify(this.httpBodyVerifyMatchingType.asSerialized);

        if (this.httpBodyType) {
            result += ',"httpBodyType":' + JsonUtil.stringify(this.httpBodyType.asSerialized);
        }

        if (this.httpBodyType && this.bodyVerify) {
            result += ',"bodyVerify":' + JsonUtil.stringify(this.bodyVerify);
        }

        result += '}';

        return result;
    }

    isEmpty(): boolean {
        return !this.httpBodyVerifyMatchingType
    }
}
