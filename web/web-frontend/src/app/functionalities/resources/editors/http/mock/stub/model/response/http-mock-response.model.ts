
import {HttpMockResponseBody} from "./http-mock-response-body.model";
import {HttpMockResponseHeader} from "./http-mock-response-header.model";
import {JsonUtil} from "../../../../../../../../utils/json.util";
import {HttpMockResponseBodyType} from "../enums/http-mock-response-body-type.enum";
import {ArrayUtil} from "../../../../../../../../utils/array.util";
import {Serializable} from "../../../../../../../../model/infrastructure/serializable.model";
import {HttpRequestHeader} from "../../../../../../../../model/resource/http/http-request-header.model";

export class HttpMockResponse implements Serializable<HttpMockResponse> {

    statusCode: number;
    headers: Array<HttpMockResponseHeader> = [];
    body: HttpMockResponseBody = new HttpMockResponseBody();
    delay: number;

    constructor() {
        this.reset();
    }

    reset(): void {

        this.statusCode = undefined;
        this.headers.length = 0;
        this.headers.push(new HttpMockResponseHeader());

        this.body.reset();
        this.delay = undefined;
    }

    deserialize(input: Object): HttpMockResponse {
        this.statusCode = input["statusCode"];

        if(input['headers']) {
            let headersObj = input['headers'];
            for (let headerProp in headersObj) {
                if (input["headers"].hasOwnProperty(headerProp)) {
                    let headerValue = headersObj[headerProp];

                    let httpRequestHeader = new HttpMockResponseHeader();
                    httpRequestHeader.key = headerProp;
                    httpRequestHeader.value = headerValue;

                    this.headers.push(
                        httpRequestHeader
                    )
                }
            }
        }

        if (input['body']) {
            this.body = new HttpMockResponseBody().deserialize(input['body']);
        }

        if(input['delay']) {
            this.delay = input['delay'];
        }

        return this;
    }

    serialize(): string {
        let result = '{';

        let shouldAddComa: boolean = false;
        if (this.statusCode) {
            shouldAddComa = true;
            result += '"statusCode":' + JsonUtil.stringify(this.statusCode);
        }

        result += ',"headers":{';
        let headers = this.getResponseHeadersWithValue();
        for (let i = 0; i < headers.length; i++) {
            let header = headers[i];
            if(i > 0) {
                result += ','
            }
            result += header.serialize()
        }
        result += '}';

        if (!this.body.isEmpty()) {
            if(shouldAddComa) result += ",";
            shouldAddComa = true;

            result += '"body":' + JsonUtil.serializeSerializable(this.body);
        }

        if (this.delay) {
            if(shouldAddComa) result += ",";
            shouldAddComa = true;

            result += '"delay":' + JsonUtil.stringify(this.delay);
        }

        result += '}';
        return result;
    }

    getResponseHeadersWithValue(): Array<HttpMockResponseHeader> {
        let result: Array<HttpMockResponseHeader> = [];
        for (let header of this.headers) {
            if (!header.isEmpty()) {
                result.push(header)
            }
        }

        return result;
    }

    setContentTypeHeader(selectedBodyType: HttpMockResponseBodyType) {
        let contentTypeHeader: HttpMockResponseHeader = this.getOrCreateContentTypeHeader();

        if(selectedBodyType == HttpMockResponseBodyType.OTHER) {
            ArrayUtil.removeElementFromArray(this.headers, contentTypeHeader);
            return;
        }

        contentTypeHeader.value = selectedBodyType.contentType;
    }

    private getOrCreateContentTypeHeader(): HttpMockResponseHeader {
        for (let responseHeader of this.headers) {
            if(responseHeader.key && responseHeader.key.toUpperCase() == HttpMockResponseBodyType.CONTENT_TYPE_HEADER_KEY.toUpperCase()) {
                return responseHeader;
            }
        }

        let emptyHeader: HttpMockResponseHeader = this.headers.filter(it => it.isEmpty()).pop();
        emptyHeader.key = HttpMockResponseBodyType.CONTENT_TYPE_HEADER_KEY;

        this.headers.push(new HttpMockResponseHeader());

        return emptyHeader;
    }
}
