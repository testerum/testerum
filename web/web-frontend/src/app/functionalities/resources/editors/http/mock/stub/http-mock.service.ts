
import {EventEmitter, Injectable} from "@angular/core";
import {HttpContentType} from "../../../../../../model/resource/http/enum/http-content-type.enum";
import {HttpMock} from "./model/http-mock.model";
import {HttpMockRequestHeader} from "./model/request/http-mock-request-header.model";

@Injectable()
export class HttpMockService {

    httpMock: HttpMock;
    editMode: boolean;
    editModeEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();
    onModelSetEventEmitter: EventEmitter<HttpMock> = new EventEmitter<HttpMock>();

    constructor() {
    }

    setModel(httpMock: HttpMock) {
        this.httpMock = httpMock;
        this.onModelSetEventEmitter.emit(httpMock)
    }

    empty() {
        Object.assign(this.httpMock, new HttpMock());
    }

    setEditMode(editMode: boolean) {
        this.editMode = editMode;
        this.editModeEventEmitter.emit(this.editMode);
    }

    getRequestContentType(): HttpContentType {
        let requestContentTypeHeader = this.getRequestContentTypeHeader();
        if(!requestContentTypeHeader) {
            return null;
        }
        return HttpContentType.fromContentType(requestContentTypeHeader.value);
    }

    getRequestContentTypeHeader(): HttpMockRequestHeader {
        let contentTypeKey = HttpContentType.CONTENT_TYPE_HEADER_KEY.toLowerCase();
        for (let header of this.httpMock.expectedRequest.headers) {
            if(header.key && header.key.toLowerCase() === contentTypeKey) {
                return header;
            }
        }

        return null;
    }

    setRequestContentType(httpContentType:HttpContentType): void {
        let contentTypeHeader = this.getRequestContentTypeHeader();
        if(contentTypeHeader == null) {
            contentTypeHeader = new HttpMockRequestHeader();
            contentTypeHeader.key = HttpContentType.CONTENT_TYPE_HEADER_KEY;
            this.httpMock.expectedRequest.headers.push(contentTypeHeader)
        }

        contentTypeHeader.value = httpContentType.contentType;
    }

    setRequestContentTypeAsString(contentTypeHeaderValue: string): void {
        let contentTypeHeader = this.getRequestContentTypeHeader();
        if(contentTypeHeader == null) {
            contentTypeHeader = new HttpMockRequestHeader();
            contentTypeHeader.key = HttpContentType.CONTENT_TYPE_HEADER_KEY;
            this.httpMock.expectedRequest.headers.push(contentTypeHeader)
        }

        contentTypeHeader.value = contentTypeHeaderValue;
    }
}
