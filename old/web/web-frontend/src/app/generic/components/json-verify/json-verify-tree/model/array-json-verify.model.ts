import {CompareMode} from "../../../../../model/enums/compare-mode.enum";
import {SerializableUnknown} from "../../../../../model/infrastructure/serializable-unknown.model";
import {SerializationUtil} from "./util/serialization.util";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeContainerSerializable} from "../../../json-tree/model/serializable/json-tree-container-serializable.model";
import {JsonTreeNodeSerializable} from "../../../json-tree/model/serializable/json-tree-node-serialzable.model";
import {Resource} from "../../../../../model/resource/resource.model";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {JsonTreeContainerOptions} from "../../../json-tree/model/behavior/JsonTreeContainerOptions";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";

export class ArrayJsonVerify extends JsonTreeContainerSerializable implements SerializableUnknown<ArrayJsonVerify>, JsonIntegrity, Resource<ArrayJsonVerify> {

    compareMode: CompareMode = CompareMode.INHERIT;
    children: Array<JsonTreeNodeSerializable> = [];
    isDirty: boolean = true;
    options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parent: JsonTreeContainer) {
        super(parent);
        this.options.displayLines = false;
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

            this.children.push(SerializationUtil.deserialize(inputItem, this) as JsonTreeNodeSerializable)
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

    reset() {
        this.compareMode = CompareMode.INHERIT;
        this.children.length = 0;
        this.isDirty = true;
    }

    isEmpty(): boolean {
        return this.children.length == 0;
    }

    clone(): ArrayJsonVerify {
        let objectAsJson = JSON.parse(this.serialize());
        return new ArrayJsonVerify(null).deserialize(objectAsJson);
    }

    createInstance(): ArrayJsonVerify {
        return new ArrayJsonVerify(null);
    }
}
