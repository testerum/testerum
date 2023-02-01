import {JsonUtil} from "../../../../../utils/json.util";
import {SerializableUnknown} from "../../../../../model/infrastructure/serializable-unknown.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeSerializable} from "../../../json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeNodeOptions} from "../../../json-tree/model/behavior/JsonTreeNodeOptions";

export class BooleanJsonVerify extends JsonTreeNodeSerializable implements SerializableUnknown<BooleanJsonVerify>, JsonIntegrity {

    value: boolean;
    isDirty: boolean = true;
    options: JsonTreeNodeOptions = new JsonTreeNodeOptions();

    constructor(parent: JsonTreeContainer) {
        super(parent);
        this.options.displayLines = false;
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

    getOptions(): JsonTreeNodeOptions {
        return this.options;
    }

    deserialize(input: Object): BooleanJsonVerify {
        this.value = input as boolean;

        return this;
    }

    serialize(): string {
        return JsonUtil.stringify(this.value);
    }
}
