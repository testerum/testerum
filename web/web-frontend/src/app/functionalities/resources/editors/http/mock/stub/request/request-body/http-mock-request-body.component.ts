import {Component, OnInit} from '@angular/core';
import {HttpMockService} from "../../http-mock.service";
import {HttpMockRequestBody} from "../../model/request/http-mock-request-body.model";
import {SerializationUtil} from "../../../../../json_verify/json-verify-tree/model/util/serialization.util";
import {JsonSchemaExtractor} from "../../../../../json_verify/json-schema/json-schema.extractor";
import {HttpMockRequestBodyMatchingType} from "../../model/enums/http-mock-request-body-matching-type.enum";
import {HttpMockRequestBodyVerifyType} from "../../model/enums/http-mock-request-body-verify-type.enum";
import {JsonVerifyTreeService} from "../../../../../json_verify/json-verify-tree/json-verify-tree.service";
import {HttpMock} from "../../model/http-mock.model";
import {EmptyJsonVerify} from "../../../../../json_verify/json-verify-tree/model/empty-json-verify.model";

@Component({
    moduleId: module.id,
    selector: 'http-mock-request-body',
    templateUrl: 'http-mock-request-body.component.html',
    styleUrls: [
        'http-mock-request-body.component.scss',
        '../../../../../../../../generic/css/generic.scss',
        '../../../../../../../../generic/css/forms.scss'
    ]
})

export class HttpMockRequestBodyComponent implements OnInit{

    sampleJsonText: string;

    aceEditorModeOptions: Array<string>=[];
    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };

    HttpMockRequestBodyMatchingType = HttpMockRequestBodyMatchingType;

    constructor(private httpMockService: HttpMockService,
                private jsonVerifyTreeService: JsonVerifyTreeService) {
    }

    ngOnInit(): void {
        this.jsonVerifyTreeService.setEmpty();
        this.jsonVerifyTreeService.editMode = this.httpMockService.editMode;
        this.httpMockService.editModeEventEmitter.subscribe(
            (editMode: boolean) => this.jsonVerifyTreeService.editMode = editMode
        );

        this.onModelSet(this.httpMockService.httpMock);
        this.httpMockService.onModelSetEventEmitter.subscribe(
            (httpMock: HttpMock) => this.onModelSet(httpMock)
        )
    }

    onModelSet(httpMock: HttpMock) {
        if(!httpMock || !httpMock.expectedRequest.body.content) {
            this.jsonVerifyTreeService.setJsonSchema(new EmptyJsonVerify())
        }

        if(httpMock.expectedRequest.body.matchingType == HttpMockRequestBodyMatchingType.JSON_VERIFY) {
            let jsonVerifyAsJson = JSON.parse(httpMock.expectedRequest.body.content);
            let jsonTreeNode = new SerializationUtil().deserialize(jsonVerifyAsJson);
            this.jsonVerifyTreeService.setJsonVerifyRootResource(jsonTreeNode);
        }

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
        switch (value) {
            case HttpMockRequestBodyMatchingType.CONTAINS: this.aceEditorModeOptions = HttpMockRequestBodyVerifyType.enums.map(it => it.toString()); break;
            case HttpMockRequestBodyMatchingType.EXACT_MATCH: this.aceEditorModeOptions = HttpMockRequestBodyVerifyType.enums.map(it => it.toString()); break;
            case HttpMockRequestBodyMatchingType.REGEX_MATCH: this.aceEditorModeOptions = [HttpMockRequestBodyVerifyType.TEXT.toString()]; break;
        }
    }

    shouldDisplayBodyTypeChooser() {
        let bodyVerifyType = this.getModel().matchingType;
        let shouldDisplay = bodyVerifyType == HttpMockRequestBodyMatchingType.CONTAINS ||
            bodyVerifyType == HttpMockRequestBodyMatchingType.EXACT_MATCH;
        return shouldDisplay;
    }

    shouldDisplayAceEditor(): boolean {
        let bodyVerifyType = this.getModel().matchingType;
        return bodyVerifyType == HttpMockRequestBodyMatchingType.CONTAINS ||
            bodyVerifyType == HttpMockRequestBodyMatchingType.EXACT_MATCH ||
            bodyVerifyType == HttpMockRequestBodyMatchingType.REGEX_MATCH;
    }

    onSampleJsonTextChange(json:string) {
        this.sampleJsonText = json;
        let jsonRootNode;
        try {
            jsonRootNode = JSON.parse(json);
        } catch (e) {
            //ignore exception, JSON is not valid
            return;
        }
        let verifyJsonRoot = new SerializationUtil().deserialize(jsonRootNode);

        let jsonSchema = new JsonSchemaExtractor().getJsonSchemaFromJson(
            verifyJsonRoot
        );

        this.jsonVerifyTreeService.setJsonSchema(jsonSchema);
    }

    shouldDisplayJsonSample(): boolean {
        return this.isEditMode() && ( this.jsonVerifyTreeService.isEmptyModel() || this.sampleJsonText != null);
    }
}
