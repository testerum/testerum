
import {JsonUtil} from "../../../../../../utils/json.util";
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {JsonTreeNodeAbstract} from "../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";
import {JsonTreeContainerOptions} from "../../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {JsonTreeNodeOptions} from "../../../../../../generic/components/json-tree/model/behavior/JsonTreeNodeOptions";

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
