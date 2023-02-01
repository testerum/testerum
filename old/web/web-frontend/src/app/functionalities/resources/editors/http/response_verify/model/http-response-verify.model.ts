
import {HttpResponseHeaderVerify} from "./http-response-header-verify.model";
import {HttpResponseBodyVerify} from "./http-response-body-verify.model";
import {JsonUtil} from "../../../../../../utils/json.util";
import {Resource} from "../../../../../../model/resource/resource.model";

export class HttpResponseVerify implements Resource<HttpResponseVerify> {

    expectedStatusCode: number;
    expectedHeaders: Array<HttpResponseHeaderVerify> = [];
    expectedBody: HttpResponseBodyVerify = new HttpResponseBodyVerify();

    constructor() {
        this.reset();
    }

    reset(): void {
        this.expectedStatusCode = undefined;
        this.expectedHeaders.length = 0;
        this.expectedHeaders.push(new HttpResponseHeaderVerify());

        this.expectedBody.reset();
    }

    isEmpty(): boolean {
        let isEmpty = true;

        if(this.expectedStatusCode) {isEmpty = false;}
        this.expectedHeaders.forEach(header => {if(!header.isEmpty()) isEmpty = false;});

        if(!this.expectedBody.isEmpty()) {isEmpty = false;}

        return isEmpty;
    }

    deserialize(input: Object): HttpResponseVerify {
        if (!input) {
            return this;
        }

        this.expectedStatusCode = input["expectedStatusCode"];

        if(input['expectedHeaders']){
            this.expectedHeaders.length = 0;
            for (let headerAsJson of input["expectedHeaders"]) {
                let header = new HttpResponseHeaderVerify().deserialize(headerAsJson);
                this.expectedHeaders.push(header)
            }
            this.expectedHeaders.push(new HttpResponseHeaderVerify());
        }

        if(input['expectedBody']) {
            this.expectedBody = new HttpResponseBodyVerify().deserialize(input['expectedBody']);
        }

        return this;
    }

    serialize(): string {
        if (this.isEmpty()) {
            return null;
        }

        let hasSavedFields = false;
        let result = '{';
        if (this.expectedStatusCode) {
            hasSavedFields = true;
            result += '"expectedStatusCode":' + JsonUtil.stringify(this.expectedStatusCode);
        }

        let headersWithValue = this.getHeadersWithValue();
        if(headersWithValue.length != 0 ) {

            if(hasSavedFields) {
                result += ',';
            }

            hasSavedFields = true;
            result += '"expectedHeaders":' + JsonUtil.serializeArrayOfSerializable(headersWithValue);
        }

        if (!this.expectedBody.isEmpty()) {
            if(hasSavedFields) {
                result += ',';
            }

            result += '"expectedBody":' + JsonUtil.serializeSerializable(this.expectedBody);
        }
        result += '}';
        return result;
    }

    getHeadersWithValue(): Array<HttpResponseHeaderVerify> {
        let result:Array<HttpResponseHeaderVerify> = [];
        for (let header of this.expectedHeaders) {
            if(!header.isEmpty()) {
                result.push(header)
            }
        }
        return result;
    }

    clone(): HttpResponseVerify {
        let objectAsJson = JSON.parse(this.serialize());
        return new HttpResponseVerify().deserialize(objectAsJson);
    }

    createInstance(): HttpResponseVerify {
        return new HttpResponseVerify();
    }
}
