import {Component, OnDestroy, OnInit} from '@angular/core';
import {HttpMockService} from "../../http-mock.service";
import {HttpMockRequestBody} from "../../model/request/http-mock-request-body.model";
import {HttpMockRequestBodyMatchingType} from "../../model/enums/http-mock-request-body-matching-type.enum";
import {HttpMockRequestBodyVerifyType} from "../../model/enums/http-mock-request-body-verify-type.enum";
import {JsonVerifyTreeService} from "../../../../../../../../generic/components/json-verify/json-verify-tree/json-verify-tree.service";
import {editor} from "monaco-editor";
import {Subscription} from "rxjs";
import {HttpBodyVerifyType} from "../../../../response_verify/model/enums/http-body-verify-type.enum";

@Component({
    moduleId: module.id,
    selector: 'http-mock-request-body',
    templateUrl: 'http-mock-request-body.component.html',
    styleUrls: [
        'http-mock-request-body.component.scss'
    ]
})
export class HttpMockRequestBodyComponent implements OnInit, OnDestroy{

    editorOptions: editor.IStandaloneEditorConstructionOptions = {};

    HttpMockRequestBodyMatchingType = HttpMockRequestBodyMatchingType;

    private editModeSubscription: Subscription;
    constructor(private httpMockService: HttpMockService) {
    }

    ngOnInit(): void {
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

    getModel(): HttpMockRequestBody {
        return this.httpMockService.httpMock.expectedRequest.body;
    }

    isEditMode(): boolean {
        return this.httpMockService.editMode;
    }

    getBodyMatchingTypes(): Array<HttpMockRequestBodyMatchingType> {
        return HttpMockRequestBodyMatchingType.enums;
    }

    getBodyVerifyTypes(): Array<HttpMockRequestBodyVerifyType> {
        return HttpMockRequestBodyVerifyType.enums;
    }

    bodyMatchingTypeChange(value: HttpMockRequestBodyMatchingType) {
        this.getModel().matchingType = value;
    }

    shouldDisplayBodyTypeChooser() {
        let bodyVerifyType = this.getModel().matchingType;
        let shouldDisplay = bodyVerifyType == HttpMockRequestBodyMatchingType.CONTAINS ||
            bodyVerifyType == HttpMockRequestBodyMatchingType.EXACT_MATCH;
        return shouldDisplay;
    }

    shouldDisplayTextEditor(): boolean {
        let bodyVerifyType = this.getModel().matchingType;
        return bodyVerifyType == HttpMockRequestBodyMatchingType.CONTAINS ||
            bodyVerifyType == HttpMockRequestBodyMatchingType.EXACT_MATCH ||
            bodyVerifyType == HttpMockRequestBodyMatchingType.REGEX_MATCH;
    }

    onTextChange(text: string) {
        this.getModel().content = text;
    }

    onBodyTypeChange(bodyType: HttpBodyVerifyType) {
        this.getModel().bodyType = bodyType;
        this.refreshEditorOptions();
    }
}
