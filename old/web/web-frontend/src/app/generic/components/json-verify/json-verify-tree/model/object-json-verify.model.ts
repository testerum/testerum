import {NodeState} from "../enum/node-state.enum";
import {CompareMode} from "../../../../../model/enums/compare-mode.enum";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {SerializableUnknown} from "../../../../../model/infrastructure/serializable-unknown.model";
import {FieldJsonVerify} from "./field-json-verify.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeContainerSerializable} from "../../../json-tree/model/serializable/json-tree-container-serializable.model";
import {JsonTreeNodeSerializable} from "../../../json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainerOptions} from "../../../json-tree/model/behavior/JsonTreeContainerOptions";
import {Serializable} from "../../../../../model/infrastructure/serializable.model";

export class ObjectJsonVerify extends JsonTreeContainerSerializable implements SerializableUnknown<ObjectJsonVerify>, JsonIntegrity {

    name: string;
    nodeState: NodeState;
    isDirty: boolean = true;
    children: Array<JsonTreeNodeSerializable> = [];
    compareMode: CompareMode = CompareMode.INHERIT;
    options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parent: JsonTreeContainer) {
        super(parent);
        this.options.displayLines = false;
    }

    getChildren(): Array<JsonTreeNodeSerializable> {
        return this.children;
    }

    hasError(): boolean {
        return this.nodeState == NodeState.MISSING_FROM_SCHEMA;
    }

    isEmptyAndShouldNotBeSaved(): boolean {
        return name == null && this.children.length == 0;
    }

    isValid(): boolean {
        let isNotValid = name == null && this.children.length != 0;
        return !isNotValid;
    }

    canDeserialize(input: any): boolean {
        return typeof input == "object";
    }

    deserialize(input: Object): ObjectJsonVerify {
        for (let key of Object.keys(input)) {
            let value = input[key];
            let fieldInput = [key, value];

            if (CompareMode.PROPERTY_NAME_IN_JSON == key) {
                this.compareMode = CompareMode.getByText(value);
                continue;
            }

            let fieldJsonVerify = new FieldJsonVerify(this).deserialize(fieldInput);
            this.children.push(fieldJsonVerify)
        }
        return this;
    }

    serialize(): string {
        if(this.children.length == 0 && this.compareMode == CompareMode.INHERIT) return "{}";

        let result = '{';
        if (this.compareMode != CompareMode.INHERIT) {
            result += '"' + CompareMode.PROPERTY_NAME_IN_JSON + '":' + this.compareMode.serialize();
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
        result += '}';

        return result;
    }
}
