import {Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges} from '@angular/core';
import {HttpRequestService} from "../http-request.service";
import {HttpRequestBody} from "../../../../../../model/resource/http/http-request-body.model";
import {HttpRequestBodyType} from "../../../../../../model/resource/http/enum/http-request-body-type.enum";
import {HttpContentType} from "../../../../../../model/resource/http/enum/http-content-type.enum";
import 'brace/mode/json';
import 'brace/mode/html';
import 'brace/mode/text';
import 'brace/mode/xml';
import 'brace/theme/eclipse';
import {Subscription} from "rxjs";

@Component({
    moduleId: module.id,
    selector: 'http-body',
    templateUrl: 'http-body.component.html',
    styleUrls: [
        'http-body.component.scss'
    ]
})
export class HttpBodyComponent implements OnInit, OnDestroy {

    @Input() body: HttpRequestBody;

    HttpBodyType = HttpRequestBodyType;
    HttpContentType = HttpContentType;

    aceEditorMode = HttpContentType.TEXT.editorMode;
    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    private changesMadeSubscription: Subscription;
    constructor(public httpRequestService: HttpRequestService) {
    }

    ngOnInit(): void {
        this.changesMadeSubscription = this.httpRequestService.changesMadeEventEmitter.subscribe(it => {
            this.onHeaderChange();
        });
    }

    ngOnDestroy(): void {
        if(this.changesMadeSubscription) this.changesMadeSubscription.unsubscribe();
    }

    setContentType(contentType:HttpContentType) {
        this.httpRequestService.setContentType(contentType);
        this.aceEditorMode = contentType.editorMode;
    }

    getContentType(): HttpContentType {
        let contentType = this.httpRequestService.getContentType();
        if(!contentType) {
            return HttpContentType.TEXT;
        }

        return contentType;
    }

    setHeaderBasedOnBodyType(event: any) {
        this.httpRequestService.setContentTypeAsString(this.body.bodyType.contentTypeHeaderValue);
    }

    onHeaderChange() {
        let contentType = this.getContentType();
        this.aceEditorMode = contentType.editorMode;
    }
}
