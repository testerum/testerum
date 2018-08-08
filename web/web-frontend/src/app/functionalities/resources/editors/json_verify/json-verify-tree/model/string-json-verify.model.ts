
import {JsonUtil} from "../../../../../../utils/json.util";
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeNodeSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeOptions} from "../../../../../../generic/components/json-tree/model/behavior/JsonTreeNodeOptions";

export class StringJsonVerify extends JsonTreeNodeSerializable implements SerializableUnknown<StringJsonVerify>, JsonIntegrity {

    value: string ;
    isDirty: boolean = true;
    options: JsonTreeNodeOptions = new JsonTreeNodeOptions();

    constructor(parent: JsonTreeContainer) {
        super(parent);
        this.options.displayLines = false;
    }

    isEmptyAndShouldNotBeSaved(): boolean {
        return this.value == null;
    }

    isValid(): boolean {
        return true;
    }

    canDeserialize(input: any): boolean {
        return typeof input == "string";
    }

    getOptions(): JsonTreeNodeOptions {
        return this.options;
    }

    deserialize(input: Object): StringJsonVerify {
        this.value = input as string;

        return this;
    }

    serialize(): string {
        return JsonUtil.stringify(this.value);
    }
}
