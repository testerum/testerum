
import {HttpMockRequestMethod} from "../enums/http-mock-request-method";
import {HttpMockRequestParam} from "./http-mock-request-param.model";
import {HttpMockRequestHeader} from "./http-mock-request-header.model";
import {HttpMockRequestBody} from "./http-mock-request-body.model";
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {HttpMockRequestScenario} from "./http-mock-request-scenario.model";
import {StringUtils} from "../../../../../../../../utils/string-utils.util";
import {HttpMockFaultResponse} from "../enums/http-mock-fault-response.enum";
import {HttpMockResponseType} from "../enums/http-mock-response-type.enum";

export class HttpMockRequest implements Serializable<HttpMockRequest> {

    method: HttpMockRequestMethod = HttpMockRequestMethod.GET;
    url: string;
    params: Array<HttpMockRequestParam> = [];
    headers: Array<HttpMockRequestHeader> = [];
    body: HttpMockRequestBody = new HttpMockRequestBody();

    scenario: HttpMockRequestScenario = new HttpMockRequestScenario();

    constructor() {
        this.reset();
    }

    reset(): void {
        this.method = HttpMockRequestMethod.GET;
        this.url = undefined;

        this.params.length = 0;
        this.headers.length = 0;
        this.params.push(new HttpMockRequestParam());
        this.headers.push(new HttpMockRequestHeader());

        this.body.reset();
    }

    isEmpty(): boolean {
        let isEmpty = true;

        if(!StringUtils.isEmpty(this.url)) {isEmpty = false;}
        this.params.forEach(param => {if(!param.isEmpty()) isEmpty = false;});
        this.headers.forEach(header => {if(!header.isEmpty()) isEmpty = false;});

        if(!this.body.isEmpty()) {isEmpty = false;}

        return isEmpty;
    }

    deserialize(input: Object): HttpMockRequest {
        if (input["method"]) {
            this.method = HttpMockRequestMethod.fromString(input["method"]);
        }

        if (input["url"]) {
            this.url = input["url"];
        }

        if (input['params']) {
            this.params.length = 0;
            for (let paramAsJson of input["params"]) {
                let param = new HttpMockRequestParam().deserialize(paramAsJson);
                this.params.push(param)
            }
            this.params.push(new HttpMockRequestParam)
        }

        if (input['headers']) {
            this.headers.length = 0;
            for (let headerAsJson of input["headers"]) {
                let header = new HttpMockRequestHeader().deserialize(headerAsJson);
                this.headers.push(header)
            }
            this.headers.push(new HttpMockRequestHeader)
        }

        if (input['body']) {
            this.body = new HttpMockRequestBody().deserialize(input['body']);
        }

        if (input['scenario']) {
            this.scenario = new HttpMockRequestScenario().deserialize(input['scenario']);
        }

        return this;
    }

    serialize(): string {
        let result = '{';
        result += '"method":' + JsonUtil.stringify(this.method.toString());

        if (this.url) {
            result += ',"url":' + JsonUtil.stringify(this.url);
        }

        let requestParamssWithValue = this.getRequestParamsWithValue();
        if (!this.hasEmptyParams()) {
            result += ',"params":' + JsonUtil.serializeArrayOfSerializable(requestParamssWithValue);
        }

        let requestHeadersWithValue = this.getRequestHeadersWithValue();
        if (!this.hasEmptyHeaders()) {
            result += ',"headers":' + JsonUtil.serializeArrayOfSerializable(requestHeadersWithValue);
        }

        if (this.method.hasBody && !this.hasEmptyBody()) {
            result += ',"body":' + JsonUtil.serializeSerializable(this.body);
        }

        if (!this.hasEmptyScenario()) {
            result += ',"scenario":' + JsonUtil.serializeSerializable(this.scenario);
        }

        result += '}';
        return result;
    }

    hasEmptyParams(): boolean {
        return this.getRequestParamsWithValue().length == 0
    }

    hasEmptyHeaders(): boolean {
        let requestHeadersWithValue = this.getRequestHeadersWithValue();
        let hasEmptyHeaders = requestHeadersWithValue.length == 0;
        return hasEmptyHeaders
    }

    hasEmptyBody(): boolean {
        return this.body.isEmpty();
    }

    hasEmptyScenario(): boolean {
        return this.scenario.isEmpty()
    }

    getRequestParamsWithValue(): Array<HttpMockRequestParam> {
        let result: Array<HttpMockRequestParam> = [];
        for (let param of this.params) {
            if (!param.isEmpty()) {
                result.push(param)
            }
        }
        return result;
    }

    getRequestHeadersWithValue(): Array<HttpMockRequestHeader> {
        let result: Array<HttpMockRequestHeader> = [];
        for (let header of this.headers) {
            if (!header.isEmpty()) {
                result.push(header)
            }
        }
        return result;
    }
}
