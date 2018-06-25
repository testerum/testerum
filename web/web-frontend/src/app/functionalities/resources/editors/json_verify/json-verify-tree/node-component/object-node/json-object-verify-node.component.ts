import {Component, Input, OnInit} from '@angular/core';
import {ObjectJsonVerify} from "../../model/object-json-verify.model";
import {JsonVerifyTreeService} from "../../json-verify-tree.service";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";
import {JsonTreeChildrenRenderer} from "../../../../../../../generic/components/json-tree/children-renderer/json-tree-children-renderer.abstract";
import {FieldJsonVerify} from "../../model/field-json-verify.model";
import {ArrayJsonVerify} from "../../model/array-json-verify.model";
import {StringJsonVerify} from "../../model/string-json-verify.model";

@Component({
    moduleId: module.id,
    selector: 'json-object-verify-node',
    templateUrl: 'json-object-verify-node.component.html',
    styleUrls:[
        'json-object-verify-node.component.css',
        '../../json-verify-tree.generic.css',
        '../../../../../../../generic/css/tree.css'
    ]
})
export class JsonObjectVerifyNodeComponent extends JsonTreeChildrenRenderer {

    @Input() model:ObjectJsonVerify;

    constructor(jsonVerifyTreeService:JsonVerifyTreeService) {
        super(jsonVerifyTreeService);
    }

    isEditMode(): boolean {
        return this.jsonVerifyTreeService.editMode;
    }

    createArrayNode() {
        let fieldJsonVerify = new FieldJsonVerify();
        fieldJsonVerify.value = new ArrayJsonVerify();
        this.model.getChildren().push(fieldJsonVerify);
    }

    createObjectNode() {
        let fieldJsonVerify = new FieldJsonVerify();
        fieldJsonVerify.value = new ObjectJsonVerify();
        this.model.getChildren().push(fieldJsonVerify);
    }

    createPrimitiveNode() {
        let fieldJsonVerify = new FieldJsonVerify();
        this.model.getChildren().push(fieldJsonVerify)
    }

    deleteEntry() {
        this.jsonVerifyTreeService.deleteEntry(this.model)
    }

    onCompareModeChange(compareMode: CompareMode) {
        if(compareMode instanceof CompareMode) {
            this.model.compareMode = compareMode;
        }
    }

    shouldHideCompareMode(): boolean {
        if(this.jsonVerifyTreeService.editMode) {
            if(this.model.compareMode != CompareMode.INHERIT) {
                return false;
            }
            return !this.hasMouseOver;
        }

        return this.model.compareMode == CompareMode.INHERIT;
    }

    displayEditModeForCompareMode(): boolean {
        if(this.jsonVerifyTreeService.editMode) {
            if(this.hasMouseOver) {
                return true;
            }
            else return false;
        }
        return false;
    }
}
