import {HttpRequestHeader} from "./http-request-header.model";
import {HttpRequestBody} from "./http-request-body.model";
import {HttpMethod} from "../../enums/http-method-enum";
import {JsonUtil} from "../../../utils/json.util";
import {Resource} from "../resource.model";
import {StringUtils} from "../../../utils/string-utils.util";

export class HttpRequest implements Resource<HttpRequest> {

    method: HttpMethod = HttpMethod.GET;
    url: string;
    headers:Array<HttpRequestHeader> = [];
    body: HttpRequestBody = new HttpRequestBody();

    constructor() {
        this.reset();
    }

    reset() {
        this.method = HttpMethod.GET;
        this.url = null;
        this.headers.length = 0;
        this.body.reset();
    }

    isEmpty(): boolean {
        let isEmpty = true;

        if(!StringUtils.isEmpty(this.url)) {isEmpty = false;}
        this.headers.forEach(header => {if(!header.isEmpty()) isEmpty = false;});

        if(!this.body.isEmpty()) {isEmpty = false;}

        return isEmpty;
    }

    deserialize(input: Object): HttpRequest {
        if (!input) {
            return this;
        }

        if (input["method"]) {
            this.method = HttpMethod.fromString(input["method"]);
        }

        this.url = input["url"];

        if(input['headers']){
            let headersObj = input['headers'];
            for (let headerProp in headersObj) {
                if (input["headers"].hasOwnProperty(headerProp)) {
                    let headerValue = headersObj[headerProp];

                    let httpRequestHeader = new HttpRequestHeader();
                    httpRequestHeader.key = headerProp;
                    httpRequestHeader.value = headerValue;

                    this.headers.push(
                        httpRequestHeader
                    )
                }
            }
        }
        this.headers.push(new HttpRequestHeader());

        if (input['body']) {
            this.body = new HttpRequestBody().deserialize(input['body']);
        }
        return this;
    }

    serialize(): string {
        if (this.isEmpty()) {
            return null;
        }

        let result = '' +
            '{' +
            '"method":' + JsonUtil.stringify(this.method.toString()) + ',' +
            '"url":' + JsonUtil.stringify(this.url) ;

        result += ',"headers":{';
        let headers = this.getHeadersWithValue();
        for (let i = 0; i < headers.length; i++) {
            let header = headers[i];
            if(i > 0) {
                result += ','
            }
            result += header.serialize()
        }
        result += '}';

        if (this.method != HttpMethod.GET && !this.body.isEmpty()) {
            result += ',"body":' + JsonUtil.serializeSerializable(this.body);
        }
        result += '}';
        return result;
    }

    getHeadersWithValue(): Array<HttpRequestHeader> {
        let result:Array<HttpRequestHeader> = [];
        for (let header of this.headers) {
            if(!header.isEmpty()) {
                result.push(header)
            }
        }

        return result;
    }

    clone(): HttpRequest {
        let objectAsJson = JSON.parse(this.serialize());
        return new HttpRequest().deserialize(objectAsJson);
    }

    createInstance(): HttpRequest {
        return new HttpRequest();
    }
}
