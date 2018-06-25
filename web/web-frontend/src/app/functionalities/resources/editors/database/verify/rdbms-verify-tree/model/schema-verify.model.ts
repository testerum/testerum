import {TreeContainerModel} from "../../../../../../../model/infrastructure/tree-container.model";
import {TableVerify} from "./table-verify.model";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";
import {JsonUtil} from "../../../../../../../utils/json.util";
import {Resource} from "../../../../../../../model/resource/resource.model";
import {StringUtils} from "../../../../../../../utils/string-utils.util";

export class SchemaVerify implements TreeContainerModel<TableVerify, any>, Resource<SchemaVerify> {

    name: string;
    compareMode: CompareMode = CompareMode.CONTAINS;
    tables: Array<TableVerify> = [];

    constructor() {
        this.reset();
    }

    reset() {
        this.name = null;
        this.compareMode = CompareMode.CONTAINS;
        this.tables.length = 0;
    }

    isEmpty(): boolean {
        let isEmpty = true;

        if(!StringUtils.isEmpty(this.name)) {isEmpty = false;}
        if(this.tables.length != 0) {isEmpty = false;}

        return isEmpty;
    }

    getChildContainers(): Array<TableVerify> {
        return this.tables;
    }

    private readonly childNodes: Array<any> = [];
    getChildNodes(): Array<any> {
        return this.childNodes;
    }

    containsTable(tableName: string): boolean {
        return this.getTableByName(tableName) != null;
    }

    getTableByName(tableName: string): TableVerify {
        for (let table of this.tables) {
            if (tableName === table.name) {
                return table;
            }
        }

        return null;
    }

    deserialize(input: Object): SchemaVerify {

        for (let tableName in input) {
            if (CompareMode.PROPERTY_NAME_IN_JSON == tableName) {
                this.compareMode = CompareMode.getByText(input[CompareMode.PROPERTY_NAME_IN_JSON]);
                continue;
            }

            this.tables.push(new TableVerify(tableName).deserialize(input[tableName]));
        }

        return this;
    }

    serialize(): string {
        let result = "" +
            '{' +
            '"' + CompareMode.PROPERTY_NAME_IN_JSON + '":' + JsonUtil.stringify(this.compareMode.getText());

        for (let table of this.tables) {
            if(table.rows.length == 0) continue;

            result += ',' + JsonUtil.stringify(table.name) + ':' + table.serialize();
        }

        result += '}';
        return result;
    }

    clone(): SchemaVerify {
        let objectAsJson = JSON.parse(this.serialize());
        return new SchemaVerify().deserialize(objectAsJson);
    }

    createInstance(): SchemaVerify {
        return new SchemaVerify();
    }

    toFormattedJSON(): string {
        let jsonAsString = this.serialize();
        let asObject = JSON.parse(jsonAsString);
        if (asObject) {
            return JSON.stringify(asObject, null, '  ');
        } else {
            return jsonAsString;
        }
    }
}
