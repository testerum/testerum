import {Component, Input, OnInit, SimpleChanges} from '@angular/core';
import {HttpResponseVerifyService} from "../http-response-verify.service";
import {HttpBodyVerifyMatchingType} from "../model/enums/http-body-verify-matching-type.enum";
import {HttpBodyVerifyType} from "../model/enums/http-body-verify-type.enum";
import {HttpResponseBodyVerify} from "../model/http-response-body-verify.model";
import {editor} from "monaco-editor";

@Component({
    moduleId: module.id,
    selector: 'http-response-verify-body',
    templateUrl: 'http-response-verify-body.component.html',
    styleUrls: [
        'http-response-verify-body.component.scss'
    ]
})
export class HttpResponseVerifyBodyComponent implements OnInit {

    @Input() expectedBody: HttpResponseBodyVerify;

    aceEditorModeOptions: Array<string>=[];

    editorOptions: editor.IEditorConstructionOptions = {
        language: HttpBodyVerifyType.TEXT.editorMode,
    };

    HttpBodyVerifyMatchingType = HttpBodyVerifyMatchingType;

    constructor(private httpResponseVerifyService: HttpResponseVerifyService) {
    }

    ngOnInit() {
        this.editorOptions = {
            language: this.expectedBody.httpBodyType.editorMode
        };
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.refreshEditorOptions();
    }

    refreshEditorOptions() {
        this.editorOptions = Object.assign(
            {},
            this.editorOptions,
            {
                language: this.expectedBody.httpBodyType.editorMode
            }
        );
    }

    isEditMode(): boolean {
        return this.httpResponseVerifyService.editMode;
    }

    getModel(): HttpResponseBodyVerify {
        return this.expectedBody;
    }

    getHttpBodyVerifyMatchingTypes(): Array<HttpBodyVerifyMatchingType> {
        return HttpBodyVerifyMatchingType.enums;
    }

    getHttpBodyVerifyTypes(): Array<HttpBodyVerifyType> {
        return HttpBodyVerifyType.enums;
    }

    bodyVerifyMatchingTypeChange(value: HttpBodyVerifyMatchingType) {
        this.expectedBody.httpBodyVerifyMatchingType = value;
        switch (value) {
            case HttpBodyVerifyMatchingType.CONTAINS: this.aceEditorModeOptions = HttpBodyVerifyType.enums.map(it => it.toString()); break;
            case HttpBodyVerifyMatchingType.EXACT_MATCH: this.aceEditorModeOptions = HttpBodyVerifyType.enums.map(it => it.toString()); break;
            case HttpBodyVerifyMatchingType.REGEX_MATCH: this.aceEditorModeOptions = [HttpBodyVerifyType.TEXT.toString()]; break;
        }
        this.refreshEditorOptions();
    }

    shouldDisplayBodyTypeChooser() {
        let bodyVerifyType = this.expectedBody.httpBodyVerifyMatchingType;
        return bodyVerifyType == HttpBodyVerifyMatchingType.CONTAINS ||
            bodyVerifyType == HttpBodyVerifyMatchingType.EXACT_MATCH
    }

    shouldDisplayAceEditor(): boolean {
        let bodyVerifyType = this.expectedBody.httpBodyVerifyMatchingType;
        return bodyVerifyType == HttpBodyVerifyMatchingType.CONTAINS ||
            bodyVerifyType == HttpBodyVerifyMatchingType.EXACT_MATCH ||
            bodyVerifyType == HttpBodyVerifyMatchingType.REGEX_MATCH;
    }

    onBeforeSave(): void {
    }

    onTextChange(text: string) {
        this.getModel().bodyVerify = text;
    }

    onBodyTypeChange(bodyType: HttpBodyVerifyType) {
        this.getModel().httpBodyType = bodyType;
        this.refreshEditorOptions();
    }
}
