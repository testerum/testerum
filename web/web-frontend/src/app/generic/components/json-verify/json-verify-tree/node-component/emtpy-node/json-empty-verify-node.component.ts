import {Component, Input} from '@angular/core';
import {ObjectJsonVerify} from "../../model/object-json-verify.model";
import {JsonVerifyTreeService} from "../../json-verify-tree.service";
import {ArrayJsonVerify} from "../../model/array-json-verify.model";
import {EmptyJsonVerify} from "../../model/empty-json-verify.model";

@Component({
    moduleId: module.id,
    selector: 'json-empty-verify-node',
    templateUrl: 'json-empty-verify-node.component.html',
    styleUrls:[
        '../../json-verify-tree.generic.scss',
        '../../../../../css/tree.scss'
    ]
})
export class JsonEmptyVerifyNodeComponent {

    @Input() model:EmptyJsonVerify;

    constructor(private jsonVerifyTreeService:JsonVerifyTreeService) {
    }

    isEditMode(): boolean {
        return this.jsonVerifyTreeService.editMode;
    }

    createArrayNode() {
        this.jsonVerifyTreeService.setJsonVerifyRootResource(new ArrayJsonVerify(this.model.parentContainer));
    }

    createObjectNode() {
        this.jsonVerifyTreeService.setJsonVerifyRootResource(new ObjectJsonVerify(this.model.parentContainer));
    }
}
