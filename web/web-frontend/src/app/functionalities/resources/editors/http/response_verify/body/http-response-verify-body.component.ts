import {Component, Input, OnInit} from '@angular/core';
import {HttpResponseVerifyService} from "../http-response-verify.service";
import {HttpBodyVerifyMatchingType} from "../model/enums/http-body-verify-matching-type.enum";
import {HttpBodyVerifyType} from "../model/enums/http-body-verify-type.enum";
import {HttpResponseBodyVerify} from "../model/http-response-body-verify.model";
import {JsonVerifyTreeService} from "../../../../../../generic/components/json-verify/json-verify-tree/json-verify-tree.service";
import {SerializationUtil} from "../../../../../../generic/components/json-verify/json-verify-tree/model/util/serialization.util";
import {JsonSchemaExtractor} from "../../../../../../generic/components/json-verify/json-schema/json-schema.extractor";

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

    sampleJsonText: string;

    aceEditorModeOptions: Array<string>=[];
    options: any = {
        printMargin: true,
        highlightActiveLine: true,
        useSoftTabs: true
    };
    HttpBodyVerifyMatchingType = HttpBodyVerifyMatchingType;

    constructor(private httpResponseVerifyService: HttpResponseVerifyService) {
    }

    ngOnInit() {
        if(this.expectedBody.httpBodyVerifyMatchingType == HttpBodyVerifyMatchingType.JSON_VERIFY) {
            let jsonVerifyAsJson = JSON.parse(this.expectedBody.bodyVerify);
            let jsonTreeNode = new SerializationUtil().deserialize(jsonVerifyAsJson);
            // this.jsonVerifyTreeService.setJsonVerifyRootResource(jsonTreeNode);
        }
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
    //
    // onSampleJsonTextChange(json:string) {
    //     this.sampleJsonText = json;
    //     let jsonRootNode;
    //     try {
    //         jsonRootNode = JSON.parse(json);
    //     } catch (e) {
    //         //ignore exception, JSON is not valid
    //         return;
    //     }
    //     let verifyJsonRoot = new SerializationUtil().deserialize(jsonRootNode);
    //
    //     let jsonSchema = new JsonSchemaExtractor().getJsonSchemaFromJson(
    //         verifyJsonRoot
    //     );
    //
    //     this.jsonVerifyTreeService.setJsonSchema(jsonSchema);
    // }
    //
    // shouldDisplayJsonSample(): boolean {
    //     return this.httpResponseVerifyService.editMode && ( this.jsonVerifyTreeService.isEmptyModel() || this.sampleJsonText != null);
    // }

    onBeforeSave(): void {
        // if(this.expectedBody.httpBodyVerifyMatchingType == HttpBodyVerifyMatchingType.JSON_VERIFY) {
        //     this.expectedBody.bodyVerify = this.jsonVerifyTreeService.rootNode.children[0].serialize();
        // }
    }
}
