import {Component, Input} from '@angular/core';
import {FieldVerify} from "../../../../model/field-verify.model";
import {RdbmsVerifyTreeService} from "../../../../rdbms-verify-tree.service";
import {ModelComponentMapping} from "../../../../../../../../../../model/infrastructure/model-component-mapping.model";

@Component({
    moduleId: module.id,
    selector: 'json-verify-field-node',
    templateUrl: 'rdbms-verify-field-node.component.html',
    styleUrls:['../../../../rdbms-verify-tree.generic.scss', '../../../../../../../../../../generic/css/tree.scss']
})
export class RdbmsVerifyFieldNodeComponent {

    @Input() model:FieldVerify;
    @Input() modelComponentMapping: ModelComponentMapping;

    rdbmsVerifyTreeService:RdbmsVerifyTreeService;

    constructor(rdbmsVerifyTreeService:RdbmsVerifyTreeService) {
        this.rdbmsVerifyTreeService = rdbmsVerifyTreeService;
    }

    deleteField(): boolean {
        return this.rdbmsVerifyTreeService.deleteField(this.model);
    }
}
