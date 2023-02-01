import {RdbmsTable} from "./rdbms-table.model";
import {Resource} from "../../resource.model";
import {Serializable} from "../../../infrastructure/serializable.model";

export class RdbmsSchema implements Serializable<RdbmsSchema> {

    name: string;
    tables: Array<RdbmsTable> = [];

    deserialize(input: Object): RdbmsSchema {
        this.name = input["name"];

        for (let tableAsJson of input["tables"]) {
            let table = new RdbmsTable().deserialize(tableAsJson);
            this.tables.push(table)
        }

        return this;
    }

    serialize(): string {
        throw new Error("Method not implemented.");
    }

    containsTable(tableName:string):boolean {
        return this.getTableByName(tableName) != null;
    }

    getTableByName(tableName: string): RdbmsTable {
        for (let table of this.tables) {
            if(tableName === table.name) {
                return table;
            }
        }

        return null;
    }

    isEmpty(): boolean {
        return this.tables.length == 0;
    }
}
