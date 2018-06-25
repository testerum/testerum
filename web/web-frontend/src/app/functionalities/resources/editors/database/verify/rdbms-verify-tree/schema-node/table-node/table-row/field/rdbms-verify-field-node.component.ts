import {Component, Input, OnInit} from '@angular/core';
import {FieldVerify} from "../../../../model/field-verify.model";
import {RdbmsVerifyTreeService} from "../../../../rdbms-verify-tree.service";

@Component({
    moduleId: module.id,
    selector: 'json-verify-table-row-node',
    templateUrl: 'rdbms-verify-field-node.component.html',
    styleUrls:['../../../../rdbms-verify-tree.generic.css', '../../../../../../../../../../generic/css/tree.css']
})
export class RdbmsVerifyFieldNodeComponent {

    @Input() model:FieldVerify;

    rdbmsVerifyTreeService:RdbmsVerifyTreeService;

    constructor(rdbmsVerifyTreeService:RdbmsVerifyTreeService) {
        this.rdbmsVerifyTreeService = rdbmsVerifyTreeService;
    }

    deleteField(): boolean {
        return this.rdbmsVerifyTreeService.deleteField(this.model);
    }
}
