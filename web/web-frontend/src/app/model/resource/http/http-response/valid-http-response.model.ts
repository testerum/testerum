import {HttpResponseHeader} from "../http-response-header.model";
import {StringUtils} from "../../../../utils/string-utils.util";
import {Resource} from "../../resource.model";
import {HttpContentType} from "../enum/http-content-type.enum";
import {HttpResponse} from "./http-response.model";

export class ValidHttpResponse implements HttpResponse, Resource<ValidHttpResponse> {

    private TEXT_MIME_TYPES: string[] = [
        "application/xml",
        "application/x-json",
        "application/json",
        "application/x-javascript",
        "application/javascript",
        "application/ecmascript"
    ];

    protocol: string;
    statusCode: number;
    headers: Array<HttpResponseHeader> = [];
    body: string;

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

        return this.body
    }

    private bodyResponseHandler(bodyAsBase64: string): string {
        if(!bodyAsBase64 || bodyAsBase64.length == 0) {
            return "";
        }

        let mimeType = this.getResponseMimeType();
        if(this.isBodyOrTypeText(mimeType)) {
            return atob(bodyAsBase64)
        }
        return bodyAsBase64;
    }

    private isBodyOrTypeText(mimeType: string): boolean {
        let lowerCaseMimeType = mimeType.toLowerCase();

        if (lowerCaseMimeType.startsWith("text/")) {
            return true;
        }
        if (lowerCaseMimeType.endsWith("+xml")) {
            return true;
        }

        return this.TEXT_MIME_TYPES.includes(lowerCaseMimeType);
    }

    private getResponseMimeType(): string {
        let contentTypeValue = this.getContentTypeValue();
        if (contentTypeValue == null) {
            return "";
        }

        return contentTypeValue.split(";")[0].trim();
    }

    private getContentTypeValue(): string {
        let contentTypeHeader = this.getContentTypeHeader();
        if(!contentTypeHeader) {
            return null;
        }
        return contentTypeHeader.values[0];
    }

    private getContentTypeHeader(): HttpResponseHeader {
        let contentTypeKey = HttpContentType.CONTENT_TYPE_HEADER_KEY.toLowerCase();
        for (let header of this.headers) {
            if(header.key && header.key.toLowerCase() === contentTypeKey) {
                return header;
            }
        }

        return null;
    }

    deserialize(input: Object): ValidHttpResponse {
        this.protocol = input["protocol"];
        this.statusCode = input["statusCode"];

        if (input['headers']) {
            for (let headerAsJson of input["headers"]) {
                let header = new HttpResponseHeader().deserialize(headerAsJson);
                this.headers.push(header)
            }
        }

        if (input['body']) {
            let bodyAsBase64 = input["body"];
            this.body = this.bodyResponseHandler(bodyAsBase64);
        }
        return this;
    }

    serialize(): string {
        throw Error('unimplemented');
    }

    clone(): ValidHttpResponse {
        let objectAsJson = JSON.parse(this.serialize());
        return new ValidHttpResponse().deserialize(objectAsJson);
    }

    createInstance(): ValidHttpResponse {
        return new ValidHttpResponse();
    }
}
