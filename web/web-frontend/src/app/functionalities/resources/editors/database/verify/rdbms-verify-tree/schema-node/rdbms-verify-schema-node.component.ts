import {Component, Input} from '@angular/core';
import {SchemaVerify} from "../model/schema-verify.model";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";
import {RdbmsVerifyTreeService} from "../rdbms-verify-tree.service";

@Component({
    moduleId: module.id,
    selector: 'rdbms-verify-schema-node',
    templateUrl: 'rdbms-verify-schema-node.component.html',
    styleUrls:['../rdbms-verify-tree.generic.scss', '../../../../../../../generic/css/tree.scss']
})

export class RdbmsVerifySchemaNodeComponent {

    @Input() model:SchemaVerify;

    rdbmsVerifyTreeService:RdbmsVerifyTreeService;

    constructor(rdbmsVerifyTreeService:RdbmsVerifyTreeService) {
        this.rdbmsVerifyTreeService = rdbmsVerifyTreeService;
    }

    onCompareModeChange(compareMode: CompareMode) {
        if(compareMode instanceof CompareMode) {
            this.model.compareMode = compareMode;
        }
    }
}
