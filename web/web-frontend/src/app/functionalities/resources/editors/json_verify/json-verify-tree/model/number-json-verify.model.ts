
import {JsonUtil} from "../../../../../../utils/json.util";
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeNodeSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";

export class NumberJsonVerify extends JsonTreeNodeSerializable implements SerializableUnknown<NumberJsonVerify>, JsonIntegrity {

    value: number;
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
        return typeof input == "number";
    }

    deserialize(input: Object): NumberJsonVerify {
        this.value = input as number;

        return this;
    }

    serialize(): string {
        return JsonUtil.stringify(this.value);
    }
}
