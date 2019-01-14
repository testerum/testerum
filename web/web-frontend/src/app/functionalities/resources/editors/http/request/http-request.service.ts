import {EventEmitter, Injectable} from "@angular/core";
import {HttpRequest} from "../../../../../model/resource/http/http-request.model";
import {HttpContentType} from "../../../../../model/resource/http/enum/http-content-type.enum";
import {HttpRequestHeader} from "../../../../../model/resource/http/http-request-header.model";
import {HttpService} from "../../../../../service/resources/http/http.service";
import {ValidHttpResponse} from "../../../../../model/resource/http/http-response/valid-http-response.model";
import {HttpResponse} from "../../../../../model/resource/http/http-response/http-response.model";
import {InvalidHttpResponse} from "../../../../../model/resource/http/http-response/invalid-http-response.model";

@Injectable()
export class HttpRequestService {

    httpRequest: HttpRequest = new HttpRequest();
    httpRequestForResponse: HttpRequest; // additional request to use to display an invalidHttpResponse, in such a way that changing the URL on the form doesn't change the error message

    shouldDisplayHttpResponseTab: boolean = false;
    validHttpResponse: ValidHttpResponse;
    invalidHttpResponse: InvalidHttpResponse;

    editMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
    changesMadeEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    constructor(private httpService:HttpService) {
    }

    executeRequest() {
        this.setHttpResponse(null);
        this.shouldDisplayHttpResponseTab = true;

        this.httpService.executeRequest(this.httpRequest).subscribe((response: HttpResponse)=> {
            this.setHttpResponse(response);
        })
    }

    isHttpResponseNull(): boolean {
        return (this.validHttpResponse == null)
            && (this.invalidHttpResponse == null);
    }

    setHttpResponse(response: HttpResponse) {
        this.validHttpResponse = null;
        this.invalidHttpResponse = null;
        this.httpRequestForResponse = this.httpRequest.clone();

        if (response instanceof ValidHttpResponse) {
            this.validHttpResponse = response;
        }
        if (response instanceof InvalidHttpResponse) {
            this.invalidHttpResponse = response;
        }

        this.changesMadeEventEmitter.emit();
    }

    setHttpRequestResource(httpRequest: HttpRequest) {
        this.httpRequest = httpRequest;
    }

    empty() {
        Object.assign(this.httpRequest, new HttpRequest());
    }

    setEditMode(editMode: boolean) {
        this.editMode = editMode;
        this.editModeEventEmitter.emit(this.editMode);
    }

    getContentType(): HttpContentType {
        let contentTypeHeader = this.getContentTypeHeader();
        if(!contentTypeHeader) {
            return null;
        }
        return HttpContentType.fromContentType(contentTypeHeader.value);
    }

    getContentTypeHeader(): HttpRequestHeader {
        let contentTypeKey = HttpContentType.CONTENT_TYPE_HEADER_KEY.toLowerCase();
        for (let header of this.httpRequest.headers) {
            if(header.key && header.key.toLowerCase() === contentTypeKey) {
                return header;
            }
        }

        return null;
    }

    setContentType(httpContentType:HttpContentType): void {
        let contentTypeHeader = this.getContentTypeHeader();
        if(contentTypeHeader == null) {
            contentTypeHeader = new HttpRequestHeader();
            contentTypeHeader.key = HttpContentType.CONTENT_TYPE_HEADER_KEY;
            this.httpRequest.headers.push(contentTypeHeader)
        }

        contentTypeHeader.value = httpContentType.contentType;
    }

    setContentTypeAsString(contentTypeHeaderValue: string): void {
        let contentTypeHeader = this.getContentTypeHeader();
        if(contentTypeHeader == null) {
            contentTypeHeader = new HttpRequestHeader();
            contentTypeHeader.key = HttpContentType.CONTENT_TYPE_HEADER_KEY;
            this.httpRequest.headers.push(contentTypeHeader)
        }

        contentTypeHeader.value = contentTypeHeaderValue;
    }
}
