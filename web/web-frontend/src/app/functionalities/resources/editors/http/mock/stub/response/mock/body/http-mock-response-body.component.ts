import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpMockService} from "../../../http-mock.service";
import {HttpMockResponseBody} from "../../../model/response/http-mock-response-body.model";
import {HttpMockResponseBodyType} from "../../../model/enums/http-mock-response-body-type.enum";
import {HttpMockResponse} from "../../../model/response/http-mock-response.model";
import {editor} from "monaco-editor";
import {Subscription} from "rxjs";
import {HttpBodyVerifyType} from "../../../../../response_verify/model/enums/http-body-verify-type.enum";

@Component({
    moduleId: module.id,
    selector: 'http-mock-response-body',
    templateUrl: 'http-mock-response-body.component.html',
    styleUrls: [
        'http-mock-response-body.component.scss'
    ]
})

export class HttpMockResponseBodyComponent implements OnInit, OnDestroy {

    editorOptions: editor.IStandaloneEditorConstructionOptions = {};

    private editModeSubscription: Subscription;
    constructor(private httpMockService:HttpMockService) {
    }

    ngOnInit() {
        this.refreshEditorOptions();

        this.editModeSubscription = this.httpMockService.editModeEventEmitter.subscribe(editMode => {
            this.refreshEditorOptions();
        });
    }

    ngOnDestroy(): void {
        if(this.editModeSubscription) this.editModeSubscription.unsubscribe();
    }

    refreshEditorOptions() {
        let language = (this.getModel() || this.getModel().bodyType) ? this.getModel().bodyType.editorMode: this.getModel().bodyType.editorMode;
        let editMode = this.httpMockService.editMode;
        this.editorOptions = Object.assign(
            {},
            this.editorOptions,
            {
                language: language,
                readOnly: !editMode
            }
        );
    }

    getModel(): HttpMockResponseBody {
        return this.httpMockService.httpMock.mockResponse.body;
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }

    getHttpMockResponseBodyTypes(): Array<HttpMockResponseBodyType> {
        return HttpMockResponseBodyType.enums;
    }

    bodyTypeChanged(bodyType: HttpMockResponseBodyType) {
        let response: HttpMockResponse = this.httpMockService.httpMock.mockResponse;
        response.setContentTypeHeader(bodyType);
        this.getModel().bodyType = bodyType;
        this.refreshEditorOptions();
    }

    onTextChange(text: string) {
        this.getModel().content = text;
    }
}
