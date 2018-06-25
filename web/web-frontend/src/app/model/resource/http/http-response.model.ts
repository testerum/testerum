import {HttpResponseHeader} from "./http-response-header.model";
import {StringUtils} from "../../../utils/string-utils.util";
import {Resource} from "../resource.model";

export class HttpResponse implements Resource<HttpResponse> {

    protocol: string;
    statusCode: number;
    headers: Array<HttpResponseHeader> = [];
    body: number[];

    constructor() {
        this.reset();
    }

    reset(): void {
        this.protocol = undefined;
        this.statusCode = undefined;
        this.headers.length = 0;
        this.body = undefined;
    }

    isEmpty(): boolean {
        let isEmpty = true;

        if(!StringUtils.isEmpty(this.protocol)) {isEmpty = false;}
        if(this.statusCode) {isEmpty = false;}
        this.headers.forEach(header => {if(!header.isEmpty()) isEmpty = false;});

        if(this.body && this.body.length != 0) {isEmpty = false;}

        return isEmpty;
    }

    bodyAsString(): string {
        if(!this.body || this.body.length == 0) {
            return "";
        }

        return StringUtils.byteArray2String(this.body)
    }

    deserialize(input: Object): HttpResponse {
        this.protocol = input["protocol"];
        this.statusCode = input["statusCode"];

        if (input['headers']) {
            for (let headerAsJson of input["headers"]) {
                let header = new HttpResponseHeader().deserialize(headerAsJson);
                this.headers.push(header)
            }
        }

        if (input['body']) {
            this.body = input["body"];
        }
        return this;
    }

    serialize(): string {
        return undefined;
    }

    clone(): HttpResponse {
        let objectAsJson = JSON.parse(this.serialize());
        return new HttpResponse().deserialize(objectAsJson);
    }

    createInstance(): HttpResponse {
        return new HttpResponse();
    }
}
