import {Component, OnInit} from '@angular/core';
import {HttpRequestService} from "../http-request.service";
import {HttpRequestBody} from "../../../../../../model/resource/http/http-request-body.model";
import {HttpRequestBodyType} from "../../../../../../model/resource/http/enum/http-request-body-type.enum";
import {HttpContentType} from "../../../../../../model/resource/http/enum/http-content-type.enum";
import 'brace/mode/json';
import 'brace/mode/html';
import 'brace/mode/text';
import 'brace/mode/xml';
import 'brace/theme/eclipse';

@Component({
    moduleId: module.id,
    selector: 'http-body',
    templateUrl: 'http-body.component.html',
    styleUrls: [
        'http-body.component.scss'
    ]
})

export class HttpBodyComponent implements OnInit {

    body: HttpRequestBody;
    httpCallService: HttpRequestService;

    HttpBodyType = HttpRequestBodyType;
    HttpContentType = HttpContentType;

    aceEditorMode = HttpContentType.TEXT.editorMode;
    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    constructor(httpCallService: HttpRequestService) {
        this.httpCallService = httpCallService;
    }

    ngOnInit() {
        this.body = this.httpCallService.httpRequest.body;
    }

    setContentType(contentType:HttpContentType) {
        this.httpCallService.setContentType(contentType);
        this.aceEditorMode = contentType.editorMode;
    }

    getContentType(): HttpContentType {
        let contentType = this.httpCallService.getContentType();
        if(!contentType) {
            return HttpContentType.TEXT;
        }

        return contentType;
    }

    setHeaderBasedOnBodyType(event: any) {
        this.httpCallService.setContentTypeAsString(this.body.bodyType.contentTypeHeaderValue);
    }
}
