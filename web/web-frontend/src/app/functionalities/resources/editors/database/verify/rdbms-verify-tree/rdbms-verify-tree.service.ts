
import {Injectable} from "@angular/core";
import {RdbmsSchema} from "../../../../../../model/resource/rdbms/schema/rdbms-schema.model";
import {SchemaVerify} from "./model/schema-verify.model";
import {SchemaAggregator} from "./aggregator/schema.aggregator";
import {TableRowVerify} from "./model/table-row-verify.model";
import {TableVerify} from "./model/table-verify.model";
import {FieldVerify} from "./model/field-verify.model";

@Injectable()
export class RdbmsVerifyTreeService {

    aggregatedSchema: SchemaVerify = new SchemaVerify(null);

    resourceSchema: SchemaVerify = new SchemaVerify(null);
    rdbmsSchema: RdbmsSchema = new RdbmsSchema();

    editMode: boolean = false;

    empty(): void {
        this.aggregatedSchema.reset();
        this.resourceSchema.reset();
        this.rdbmsSchema = new RdbmsSchema();
    }

    setSchemaVerifyResource(schemaVefiry: SchemaVerify) {
        this.resourceSchema = schemaVefiry;
        this.refreshTree();
    }

    setRdbmsSchema(schema: RdbmsSchema) {
        this.rdbmsSchema = schema;

        this.refreshTree()
    }

    refreshTree() {
        new SchemaAggregator(this.aggregatedSchema, this.rdbmsSchema, this.resourceSchema).aggregate();
    }


    deleteTable(tableToDelete: TableVerify): boolean {

        let index = this.aggregatedSchema.tables.indexOf(tableToDelete, 0);
        if (index > -1) {
            this.aggregatedSchema.tables.splice(index, 1);
            return true;
        }

        return false;
    }

    deleteTableRow(tableRowToDelete: TableRowVerify): boolean {
        for (let table of this.aggregatedSchema.tables) {
            let index = table.rows.indexOf(tableRowToDelete, 0);
            if (index > -1) {
                table.rows.splice(index, 1);
                return true;
            }
        }

        return false;
    }

    deleteField(fieldToDelete: FieldVerify) {
        for (let table of this.aggregatedSchema.tables) {
            for (let row of table.rows) {
                let index = row.fields.indexOf(fieldToDelete, 0);
                if (index > -1) {
                    row.fields.splice(index, 1);
                    return true;
                }
            }
        }

        return false;
    }
}
