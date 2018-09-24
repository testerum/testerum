import {Component, OnInit} from '@angular/core';
import {SchemaVerify} from "./model/schema-verify.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {RdbmsVerifySchemaNodeComponent} from "./schema-node/rdbms-verify-schema-node.component";
import {RdbmsVerifyTableNodeComponent} from "./schema-node/table-node/rdbms-verify-table-node.component";
import {TableVerify} from "./model/table-verify.model";
import {TableRowVerify} from "./model/table-row-verify.model";
import {RdbmsVerifyTableRowNodeComponent} from "./schema-node/table-node/table-row/rdbms-verify-table-row-node.component";
import {FieldVerify} from "./model/field-verify.model";
import {RdbmsVerifyFieldNodeComponent} from "./schema-node/table-node/table-row/field/rdbms-verify-field-node.component";
import {RdbmsVerifyTreeService} from "./rdbms-verify-tree.service";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'rdbms-verify-tree',
    templateUrl: 'rdbms-verify-tree.component.html'
})
export class RdbmsVerifyTreeComponent implements OnInit {

    treeModel: JsonTreeModel;

    modelComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(SchemaVerify, RdbmsVerifySchemaNodeComponent)
        .addPair(TableVerify, RdbmsVerifyTableNodeComponent)
        .addPair(TableRowVerify, RdbmsVerifyTableRowNodeComponent)
        .addPair(FieldVerify, RdbmsVerifyFieldNodeComponent);

    constructor(private verifyTreeService:RdbmsVerifyTreeService) {
    }

    ngOnInit() {
        this.treeModel = new JsonTreeModel();
        let aggregatedSchema = this.verifyTreeService.aggregatedSchema;
        aggregatedSchema.parentContainer = this.treeModel;

        this.treeModel.getChildren().push(aggregatedSchema)
    }

    hasTreeAnyNodes(): boolean {
        let rootNode = this.treeModel.getChildren()[0];
        if (!rootNode) {
            return true;
        }

        return (rootNode as JsonTreeContainer).getChildren().length > 0;
    }
}
