import {FieldVerify} from "./field-verify.model";
import {TreeContainerModel} from "../../../../../../../model/infrastructure/tree-container.model";
import {RdbmsTable} from "../../../../../../../model/resource/rdbms/schema/rdbms-table.model";
import {NodeState} from "../enum/node-state.enum";
import {CompareMode} from "../../../../../../../model/enums/compare-mode.enum";
import {Serializable} from "../../../../../../../model/infrastructure/serializable.model";
import {JsonTreeNode} from "../../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {JsonTreeNodeAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeContainerAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-container.abstract";

export class TableRowVerify extends JsonTreeContainerAbstract implements Serializable<TableRowVerify> {

    name: string;
    fields: Array<FieldVerify> = [];
    compareMode: CompareMode = CompareMode.INHERIT;

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }

    static createInstanceFromTableVerifyModel(resourceTableEntryVerify: TableRowVerify): TableRowVerify {
        let instance = new TableRowVerify(resourceTableEntryVerify.getParent());
        instance.name = resourceTableEntryVerify.name;
        instance.compareMode = resourceTableEntryVerify.compareMode ? resourceTableEntryVerify.compareMode : instance.compareMode;

        for (let field of resourceTableEntryVerify.fields) {
            instance.fields.push(
                FieldVerify.createInstanceFromFieldVerifyModel(field)
            );
        }

        return instance;
    }

    static createInstanceFromRdbmsTable(parentContainer: JsonTreeContainer, rdbmsTable: RdbmsTable): TableRowVerify {
        let instance = new TableRowVerify(parentContainer);
        instance.name = rdbmsTable.name;

        for (let field of rdbmsTable.fields) {
            instance.fields.push(
                FieldVerify.createInstanceFromRdbmsField(instance, field, NodeState.UNUSED)
            );
        }
        return instance
    }

    getChildren(): Array<FieldVerify> {
        return this.fields;
    }

    containsField(fieldName: string): boolean {
        return this.getFieldByName(fieldName) != null;
    }

    getFieldByName(fieldName: string): FieldVerify {
        for (let field of this.fields) {
            if (field.name === fieldName) {
                return field;
            }
        }
        return null;
    }

    isUsed(): boolean {
        for (let field of this.fields) {
            if (field.value) {
                return true;
            }
        }
        return false;
    }

    deserialize(input: Object): TableRowVerify {
        for (let fieldName in input) {

            if (CompareMode.PROPERTY_NAME_IN_JSON == fieldName) {
                this.compareMode = CompareMode.getByText(input[CompareMode.PROPERTY_NAME_IN_JSON]);
                continue;
            }

            this.fields.push(new FieldVerify(this, fieldName).deserialize(input[fieldName]));
        }
        return this;
    }

    serialize(): string {

        let fieldsWithValue = this.getFieldsWithValue();

        if(fieldsWithValue.length == 0) return "";

        let result = '{';

        if (this.compareMode != CompareMode.INHERIT) {
            result += '"' + CompareMode.PROPERTY_NAME_IN_JSON + '":' + this.compareMode.serialize() + ',';
        }

        let index = 0;
        for (let field of fieldsWithValue) {
            if (index != 0) result += ',';
            result += field.serialize();
            index++;
        }
        result += '}';

        return result;
    }

    private getFieldsWithValue(): Array<FieldVerify> {

        let result: Array<FieldVerify> = [];
        let index = 0;
        for (let field of this.fields) {
            if (!field.value) continue;

            result.push(field);
        }

        return result;
    }
}
