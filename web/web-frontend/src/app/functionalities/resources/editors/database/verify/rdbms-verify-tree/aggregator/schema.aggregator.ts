import {SchemaVerify} from "../model/schema-verify.model";
import {RdbmsSchema} from "../../../../../../../model/resource/rdbms/schema/rdbms-schema.model";
import {TableVerify} from "../model/table-verify.model";
import {FieldVerify} from "../model/field-verify.model";
import {NodeState} from "../enum/node-state.enum";
import {TableRowVerify} from "../model/table-row-verify.model";
import {ArrayUtil} from "../../../../../../../utils/array.util";

export class SchemaAggregator {

    private aggregatedSchema: SchemaVerify;
    private rdbmsSchema: RdbmsSchema;
    private resourceSchema: SchemaVerify;

    constructor(aggregatedSchema: SchemaVerify, rdbmsSchema: RdbmsSchema, resourceSchema: SchemaVerify) {
        this.aggregatedSchema = aggregatedSchema ? aggregatedSchema : new SchemaVerify(null);
        this.rdbmsSchema = rdbmsSchema ? rdbmsSchema : new RdbmsSchema;
        this.resourceSchema = resourceSchema ? resourceSchema : new SchemaVerify(null);
    }

    public aggregate(): void {

        this.aggregateSchemaName();
        this.aggregateSchemaCompareMode();

        this.aggregateTables();
        this.aggregateFields();

        this.orderAggregatedSchema();
    }

    private aggregateSchemaName() {
        if (this.rdbmsSchema.name) {
            this.aggregatedSchema.name = this.rdbmsSchema.name;
        } else {
            this.aggregatedSchema.name = this.resourceSchema.name;
        }
    };

    private aggregateSchemaCompareMode() {
        this.aggregatedSchema.compareMode = this.resourceSchema.compareMode
    };

    private aggregateTables(): void {
        this.removeUnexistingTablesFromAggregator();
        this.addTablesFromRdbmsSchema();
        this.addTablesFromResourceSchema();
    }

    private removeUnexistingTablesFromAggregator() {
        let tablesToRemove: Array<TableVerify> = [];

        for (let aggTable of this.aggregatedSchema.tables) {
            if (!this.rdbmsSchema.containsTable(aggTable.name)
                && !this.resourceSchema.containsTable(aggTable.name)) {
                tablesToRemove.push(aggTable)
            } else {
                if(this.rdbmsSchema.isEmpty()) {
                    aggTable.nodeState = NodeState.USED;
                } else {
                    aggTable.nodeState = NodeState.MISSING_FROM_SCHEMA;
                }
            }
        }

        for (let table of tablesToRemove) {
            ArrayUtil.removeElementFromArray(this.aggregatedSchema.tables, table);
        }
    }

    private addTablesFromRdbmsSchema() {
        for (let rdbmsTable of this.rdbmsSchema.tables) {
            let aggTable = this.aggregatedSchema.getTableByName(rdbmsTable.name);
            let resTable = this.resourceSchema.containsTable(rdbmsTable.name);

            if (aggTable == null) {

                let nodeState = resTable ? NodeState.USED : NodeState.UNUSED;

                this.aggregatedSchema.tables.push(
                    TableVerify.createInstance(
                        this.aggregatedSchema,
                        rdbmsTable.name,
                        nodeState
                    )
                )
            } else {
                aggTable.nodeState = NodeState.USED;
            }
        }
    }

    private addTablesFromResourceSchema() {
        for (let resTable of this.resourceSchema.tables) {
            let aggTable = this.aggregatedSchema.getTableByName(resTable.name);
            let rdbmsTable = this.rdbmsSchema.containsTable(resTable.name);

            if (aggTable == null) {
                let nodeState = rdbmsTable ? NodeState.UNUSED : NodeState.USED;

                this.aggregatedSchema.tables.push(
                    TableVerify.createInstanceFromTableVerifyModel(this.aggregatedSchema, resTable, nodeState)
                )
            } else {
                // aggTable.nodeState = NodeState.USED;
            }
        }
    }

    private aggregateFields() {
        for (let aggTable of this.aggregatedSchema.tables) {
            this.removeUnexistingRowsFromAggregator(aggTable);
            this.removeUnexistingFieldsFromAggregator(aggTable);
            this.addRowsAndFieldsFromResourceSchema(aggTable);
            this.aggregateFieldsFromRdbmsSchema(aggTable);
        }
    }

    private removeUnexistingRowsFromAggregator(aggTable: TableVerify) {
        let resTable = this.resourceSchema.getTableByName(aggTable.name);

        if (resTable && aggTable.rows.length > resTable.rows.length) {
            aggTable.rows.length = resTable.rows.length;
        }
    }

    private removeUnexistingFieldsFromAggregator(aggTable: TableVerify) {
        let rdbmsTable = this.rdbmsSchema.getTableByName(aggTable.name);
        let resTable = this.resourceSchema.getTableByName(aggTable.name);


        let rowIndex = 0;
        for (let aggRow of aggTable.rows) {
            let fieldsToBeRemoved: Array<FieldVerify> = [];

            for (let aggField of aggRow.fields) {
                if ((!rdbmsTable || !rdbmsTable.containsField(aggField.name))
                    && (!resTable || !resTable.containsField(rowIndex, aggField.name))) {
                    fieldsToBeRemoved.push(aggField)
                } else {
                    if (this.rdbmsSchema.tables.length != 0) {
                        aggField.nodeState = NodeState.MISSING_FROM_SCHEMA;
                    }
                }
            }

            for (let fieldToBeRemoved of fieldsToBeRemoved) {
                ArrayUtil.removeElementFromArray(aggRow.fields, fieldToBeRemoved);
            }
            rowIndex++;
        }
    }

    private addRowsAndFieldsFromResourceSchema(aggTable: TableVerify) {
        let resTable = this.resourceSchema.getTableByName(aggTable.name);
        if(!resTable) return;

        for (let i = aggTable.rows.length; i < resTable.rows.length; i++) {
            let resRow = resTable.rows[i];
            let aggRow = TableRowVerify.createInstanceFromTableVerifyModel(aggTable, resRow);
            aggTable.rows.push(aggRow);
        }
    }

    private aggregateFieldsFromRdbmsSchema(aggTable: TableVerify) {
        let rdbmsTable = this.rdbmsSchema.getTableByName(aggTable.name);

        if(rdbmsTable == null) {
            return;
        }

        for (let aggRow of aggTable.getChildren()) {
            for (let rdbmsField of rdbmsTable.fields) {
                let aggField = aggRow.getFieldByName(rdbmsField.name);
                if (aggField == null) {
                    aggRow.getChildren().push(
                        FieldVerify.createInstanceFromRdbmsField(aggRow, rdbmsField, NodeState.UNUSED)
                    );
                } else {
                    aggField.updateFromRdbmsField(rdbmsField)
                }
            }
        }
    }

    private orderAggregatedSchema() {
        let tables = this.aggregatedSchema.tables;
        tables.sort((a, b) => {
            if (a.name > b.name) return 1;
            if (a.name < b.name) return -1;
            return 0;
        })
    }
}
