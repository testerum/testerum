import {JsonUtil} from "../../../../../utils/json.util";
import {SerializableUnknown} from "../../../../../model/infrastructure/serializable-unknown.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeNodeSerializable} from "../../../json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainer} from "../../../json-tree/model/json-tree-container.model";
import {JsonTreeNodeOptions} from "../../../json-tree/model/behavior/JsonTreeNodeOptions";

export class NumberJsonVerify extends JsonTreeNodeSerializable implements SerializableUnknown<NumberJsonVerify>, JsonIntegrity {

    value: number;
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
        return typeof input == "number";
    }

    getOptions(): JsonTreeNodeOptions {
        return this.options;
    }

    deserialize(input: Object): NumberJsonVerify {
        this.value = input as number;

        return this;
    }

    serialize(): string {
        return JsonUtil.stringify(this.value);
    }
}
