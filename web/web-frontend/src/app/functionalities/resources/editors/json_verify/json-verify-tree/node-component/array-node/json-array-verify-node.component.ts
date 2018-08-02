import {Component, Input} from '@angular/core';
import {ArrayJsonVerify} from "../../model/array-json-verify.model";
import {JsonVerifyTreeService} from "../../json-verify-tree.service";
import {JsonTreeChildrenRenderer} from "../../../../../../../generic/components/json-tree/children-renderer/json-tree-children-renderer.abstract";
import {ObjectJsonVerify} from "../../model/object-json-verify.model";
import {StringJsonVerify} from "../../model/string-json-verify.model";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";

@Component({
    moduleId: module.id,
    selector: 'json-array-verify-node',
    templateUrl: 'json-array-verify-node.component.html',
    styleUrls:[
        'json-array-verify-node.component.scss',
        '../../json-verify-tree.generic.scss',
        '../../../../../../../generic/css/tree.scss'
    ]
})

export class JsonArrayVerifyNodeComponent extends JsonTreeChildrenRenderer {

    @Input() model:ArrayJsonVerify;

    constructor(jsonVerifyTreeService:JsonVerifyTreeService) {
        super(jsonVerifyTreeService);
    }

    isEditMode(): boolean {
        return this.jsonVerifyTreeService.editMode;
    }

    createArrayNode() {
        this.model.getChildren().push(new ArrayJsonVerify());
    }

    createObjectNode() {
        this.model.getChildren().push(new ObjectJsonVerify());
    }

    createPrimitiveNode() {
        this.model.getChildren().push(new StringJsonVerify())
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
