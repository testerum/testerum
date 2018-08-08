
import {JsonUtil} from "../../../../../../utils/json.util";
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {JsonTreeNodeAbstract} from "../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";

export class BooleanJsonVerify extends JsonTreeNodeAbstract implements SerializableUnknown<BooleanJsonVerify>, JsonIntegrity {

    value: boolean;
    isDirty: boolean = true;

    constructor(parent: JsonTreeContainer) {
        super(parent);
    }

    isEmptyAndShouldNotBeSaved(): boolean {
        return !(typeof this.value == "boolean");
    }

    isValid(): boolean {
        return true;
    }

    canDeserialize(input: any): boolean {
        return typeof input == "boolean";
    }

    deserialize(input: Object): BooleanJsonVerify {
        this.value = input as boolean;

        return this;
    }

    serialize(): string {
        return JsonUtil.stringify(this.value);
    }
}
