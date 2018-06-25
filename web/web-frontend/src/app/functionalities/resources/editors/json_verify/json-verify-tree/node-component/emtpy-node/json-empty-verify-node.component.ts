import {Component, Input, OnInit} from '@angular/core';
import {ObjectJsonVerify} from "../../model/object-json-verify.model";
import {JsonVerifyTreeService} from "../../json-verify-tree.service";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";
import {JsonTreeChildrenRenderer} from "../../../../../../../generic/components/json-tree/children-renderer/json-tree-children-renderer.abstract";
import {FieldJsonVerify} from "../../model/field-json-verify.model";
import {ArrayJsonVerify} from "../../model/array-json-verify.model";
import {JsonTreeNodeAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {StringJsonVerify} from "../../model/string-json-verify.model";
import {NumberJsonVerify} from "../../model/number-json-verify.model";
import {NullJsonVerify} from "../../model/null-json-verify.model";
import {BooleanJsonVerify} from "../../model/boolean-json-verify.model";
import {EmptyJsonVerify} from "../../model/empty-json-verify.model";

@Component({
    moduleId: module.id,
    selector: 'json-empty-verify-node',
    templateUrl: 'json-empty-verify-node.component.html',
    styleUrls:['../../json-verify-tree.generic.css', '../../../../../../../generic/css/tree.css', '../../../../../../../generic/css/generic.css']
})
export class JsonEmptyVerifyNodeComponent {

    @Input() model:EmptyJsonVerify;

    constructor(private jsonVerifyTreeService:JsonVerifyTreeService) {
    }

    isEditMode(): boolean {
        return this.jsonVerifyTreeService.editMode;
    }

    createArrayNode() {
        this.jsonVerifyTreeService.setJsonVerifyRootResource(new ArrayJsonVerify());
    }

    createObjectNode() {
        this.jsonVerifyTreeService.setJsonVerifyRootResource(new ObjectJsonVerify());
    }
}
