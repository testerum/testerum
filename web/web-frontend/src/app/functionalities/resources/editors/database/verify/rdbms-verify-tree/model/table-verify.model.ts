import {FieldVerify} from "./field-verify.model";
import {TreeContainerModel} from "../../../../../../../model/infrastructure/tree-container.model";
import {TableRowVerify} from "./table-row-verify.model";
import {NodeState} from "../enum/node-state.enum";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";
import {JsonUtil} from "../../../../../../../utils/json.util";
import {Serializable} from "../../../../../../../model/infrastructure/serializable.model";
import {JsonTreeContainerAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";

export class TableVerify extends JsonTreeContainerAbstract implements Serializable<TableVerify> {

    name: string;
    rows: Array<TableRowVerify> = [];
    nodeState: NodeState;
    compareMode: CompareMode = CompareMode.INHERIT;

    constructor(parentContainer: JsonTreeContainer, tableName: string) {
        super(parentContainer);
        this.name = tableName;
    }

    static createInstance(parentContainer: JsonTreeContainer, name: string, nodeState: NodeState): TableVerify {
        let instance = new TableVerify(parentContainer, name);
        instance.nodeState = nodeState;
        return instance;
    }

    static createInstanceFromTableVerifyModel(resTable: TableVerify, nodeState: NodeState) {
        let instance = new TableVerify(resTable.parentContainer, resTable.name);
        instance.nodeState = nodeState;
        instance.compareMode = resTable.compareMode ? resTable.compareMode : instance.compareMode;

        for (let field of resTable.fields) {
            instance.fields.push(
                FieldVerify.createInstanceFromFieldVerifyModel(field)
            );
        }

        return instance;
    }

    getChildren(): Array<TableRowVerify> {
        return this.rows;
    }

    private readonly fields: Array<any> = [];
    getChildNodes(): Array<any> {
        return this.fields;
    }

    containsField(rowIndex: number, name: string) {
        return this.getField(rowIndex, name) != null;
    }

    getField(rowIndex: number, name: string) {
        if (this.rows.length <= rowIndex) {
            return null;
        }

        let tableEntryVerify = this.rows[rowIndex];
        return tableEntryVerify.getFieldByName(name);
    }

    isUsed(): boolean {
        return this.rows.length > 0
    }

    hasError(): boolean {
        return this.nodeState == NodeState.MISSING_FROM_SCHEMA;
    }


    deserialize(input: Object): TableVerify {
        for (let rowIndex in input) {

            let tableRow = input[rowIndex];
            let isCompareModeString = typeof tableRow === 'string' && tableRow.includes(CompareMode.PROPERTY_NAME_IN_JSON);
            if (isCompareModeString) {
                this.compareMode = CompareMode.deserializeFromJsonString(tableRow);
                continue;
            }

            this.rows.push(new TableRowVerify(this).deserialize(input[rowIndex]));
        }
        return this;
    }

    serialize(): string {
        if(this.rows.length == 0) return "";

        let result = '[';
        if (this.compareMode != CompareMode.INHERIT) {
            result += '"' + CompareMode.PROPERTY_NAME_IN_JSON + ':' + this.compareMode.getText() + '"';
            if(this.rows.length != 0) {
                result += ',';
            }
        }

        let index = 0;
        for (let row of this.rows) {
            if (index != 0) result += ',';
            result += row.serialize();
            index++;
        }
        result += ']';

        return result;
    }
}
