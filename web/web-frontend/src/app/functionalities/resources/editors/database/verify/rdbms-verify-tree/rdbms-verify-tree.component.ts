import {Component, OnInit} from '@angular/core';
import {RdbmsSchema} from "../../../../../../model/resource/rdbms/schema/rdbms-schema.model";
import {SchemaVerify} from "./model/schema-verify.model";
import {SchemaAggregator} from "./aggregator/schema.aggregator";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {RdbmsVerifySchemaNodeComponent} from "./schema-node/rdbms-verify-schema-node.component";
import {TreeModel} from "../../../../../../generic/components/tree/model/tree.model";
import {RdbmsVerifyTableNodeComponent} from "./schema-node/table-node/rdbms-verify-table-node.component";
import {TableVerify} from "./model/table-verify.model";
import {TableRowVerify} from "./model/table-row-verify.model";
import {RdbmsVerifyTableRowNodeComponent} from "./schema-node/table-node/table-row/rdbms-verify-table-row-node.component";
import {FieldVerify} from "./model/field-verify.model";
import {RdbmsVerifyFieldNodeComponent} from "./schema-node/table-node/table-row/field/rdbms-verify-field-node.component";
import {RdbmsVerifyTreeService} from "./rdbms-verify-tree.service";

@Component({
    moduleId: module.id,
    selector: 'rdbms-verify-tree',
    templateUrl: 'rdbms-verify-tree.component.html'
})

export class RdbmsVerifyTreeComponent implements OnInit {

    treeModel: TreeModel<SchemaVerify,any>;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(SchemaVerify, RdbmsVerifySchemaNodeComponent)
        .addPair(TableVerify, RdbmsVerifyTableNodeComponent)
        .addPair(TableRowVerify, RdbmsVerifyTableRowNodeComponent)
        .addPair(FieldVerify, RdbmsVerifyFieldNodeComponent);

    constructor(private verifyTreeService:RdbmsVerifyTreeService) {
    }

    ngOnInit() {
        this.treeModel = new TreeModel<SchemaVerify,any>();
        this.treeModel.childContainers.push(this.verifyTreeService.aggregatedSchema)
    }
}
