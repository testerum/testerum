import {SerializableUnknown} from "../../../../../model/infrastructure/serializable-unknown.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeNodeSerializable} from "../../../json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeOptions} from "../../../json-tree/model/behavior/JsonTreeNodeOptions";

export class NullJsonVerify extends JsonTreeNodeSerializable implements SerializableUnknown<NullJsonVerify>, JsonIntegrity {

    value: any;
    isDirty: boolean = true;
    options: JsonTreeNodeOptions = new JsonTreeNodeOptions();

    constructor(parent: JsonTreeContainer) {
        super(parent);
        this.options.displayLines = false;
    }

    isEmptyAndShouldNotBeSaved(): boolean {
        return false;
    }

    isValid(): boolean {
        return true;
    }

    canDeserialize(input: any): boolean {
        return input === null;
    }

    getOptions(): JsonTreeNodeOptions {
        return this.options;
    }

    deserialize(input: Object): NullJsonVerify {
        this.value = input;

        return this;
    }

    serialize(): string {
        return "null";
    }
}
