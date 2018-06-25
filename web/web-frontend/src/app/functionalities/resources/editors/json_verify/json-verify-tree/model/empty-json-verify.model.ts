
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeNodeSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";

export class EmptyJsonVerify extends JsonTreeNodeSerializable implements SerializableUnknown<EmptyJsonVerify>, JsonIntegrity {

    isDirty: boolean = true;

    constructor() {
        super(null);
    }

    isEmptyAndShouldNotBeSaved(): boolean {
        return false;
    }

    isValid(): boolean {
        return true;
    }

    canDeserialize(input: any): boolean {
        return typeof input === 'undefined';
    }

    deserialize(input: Object): EmptyJsonVerify {
        return this;
    }

    serialize(): string {
        return "";
    }
}
