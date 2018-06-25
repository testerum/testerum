
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeNodeSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";

export class NullJsonVerify extends JsonTreeNodeSerializable implements SerializableUnknown<NullJsonVerify>, JsonIntegrity {

    value: any;
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
        return input === null;
    }

    deserialize(input: Object): NullJsonVerify {
        this.value = input;

        return this;
    }

    serialize(): string {
        return "null";
    }
}
