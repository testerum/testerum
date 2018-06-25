
import {EventEmitter, Injectable} from "@angular/core";
import {HttpRequest} from "../../../../../model/resource/http/http-request.model";
import {HttpContentType} from "../../../../../model/resource/http/enum/http-content-type.enum";
import {HttpRequestHeader} from "../../../../../model/resource/http/http-request-header.model";
import {HttpService} from "../../../../../service/resources/http/http.service";
import {HttpResponse} from "../../../../../model/resource/http/http-response.model";

@Injectable()
export class HttpRequestService {

    httpRequest: HttpRequest;
    httpResponse: HttpResponse;
    editMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    constructor(private httpService:HttpService) {
    }

    executeRequest() {
        this.httpService.executeRequest(this.httpRequest).subscribe(response => {
            this.httpResponse = response;
        })
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
