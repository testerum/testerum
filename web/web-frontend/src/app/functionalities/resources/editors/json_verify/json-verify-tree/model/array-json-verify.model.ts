import {CompareMode} from "../../../../../../model/enums/compare-mode.enum";
import {JsonUtil} from "../../../../../../utils/json.util";
import {JsonTreeNode} from "../../../../../../generic/components/json-tree/model/json-tree-node.model";
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {SerializationUtil} from "./util/serialization.util";
import {JsonTreeContainerAbstract} from "../../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeContainerSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-container-serializable.model";
import {JsonTreeNodeSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";

export class ArrayJsonVerify extends JsonTreeContainerSerializable implements SerializableUnknown<ArrayJsonVerify>, JsonIntegrity {

    compareMode: CompareMode = CompareMode.INHERIT;
    children: Array<JsonTreeNodeSerializable> = [];
    isDirty: boolean = true;

    constructor() {
        super(null);
    }

    getChildren(): Array<JsonTreeNodeSerializable> {
        return this.children;
    }

    isEmptyAndShouldNotBeSaved(): boolean {
        return false;
    }

    isValid(): boolean {
        return true;
    }

    canDeserialize(input: any): boolean {
        return Array.isArray(input);
    }

    deserialize(input: Object): ArrayJsonVerify {
        for (let inputItem of input as Array<any>) {

            let isCompareModeString = typeof inputItem === 'string' && inputItem.includes(CompareMode.PROPERTY_NAME_IN_JSON);
            if (isCompareModeString) {
                this.compareMode = CompareMode.deserializeFromJsonString(inputItem);
                continue;
            }

            this.children.push(SerializationUtil.deserialize(inputItem) as JsonTreeNodeSerializable)
        }
        return this;
    }

    serialize(): string {
        let result = '[' ;
        if (this.compareMode != CompareMode.INHERIT) {
            result += '"' + CompareMode.PROPERTY_NAME_IN_JSON + ':' + this.compareMode.getText() + '"';
            if(this.children.length != 0) {
                result += ',';
            }
        }

        let index = 0;
        for (let child of this.children) {
            if (index != 0) result += ',';
            result += (child as Serializable<any>).serialize();
            index++;
        }

        result += ']';
        return result;
    }
}
