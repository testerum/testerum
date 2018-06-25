import {NodeState} from "../enum/node-state.enum";
import {JsonUtil} from "../../../../../../utils/json.util";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {SerializableUnknown} from "../../../../../../model/infrastructure/serializable-unknown.model";
import {SerializationUtil} from "./util/serialization.util";
import {ArrayJsonVerify} from "./array-json-verify.model";
import {ObjectJsonVerify} from "./object-json-verify.model";
import {BooleanJsonVerify} from "./boolean-json-verify.model";
import {NullJsonVerify} from "./null-json-verify.model";
import {NumberJsonVerify} from "./number-json-verify.model";
import {StringJsonVerify} from "./string-json-verify.model";
import {JsonIntegrity} from "./infrastructure/json-integrity.interface";
import {CompareMode} from "../../../../../../model/enums/compare-mode.enum";
import {JsonTreeContainerSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-container-serializable.model";
import {JsonTreeNodeSerializable} from "../../../../../../generic/components/json-tree/model/serializable/json-tree-node-serialzable.model";

export class FieldJsonVerify extends JsonTreeContainerSerializable implements SerializableUnknown<FieldJsonVerify>, JsonIntegrity {
    name: string;

    value: any;
    nodeState: NodeState;

    isDirty: boolean = true;

    compareMode: CompareMode = CompareMode.INHERIT;

    constructor() {
        super(null);
    }

    getChildren(): Array<JsonTreeNodeSerializable> {
        if (!this.value) {
            return []
        }

        return [this.value];
    }

    hasError(): boolean {
        return this.nodeState == NodeState.MISSING_FROM_SCHEMA;
    }

    isEmptyAndShouldNotBeSaved(): boolean {
        let isEmptyNode = this.name == null &&
            (this.value == null || (this.value.isContainer() && this.isEmptyValue()));
        return isEmptyNode || !this.isDirty;
    }

    isValid(): boolean {
        if(!this.isDirty) { return true }

        let isEmpty = (this.name == null && (this.value == null || (this.value.isContainer() && this.isEmptyValue())));
        return isEmpty || (this.name != null && this.value != null);
    }

    getValueOfPrimitiveType(): string {

        if (this.value instanceof BooleanJsonVerify) {
            return (this.value as BooleanJsonVerify).value ? "true" : "false";
        }
        if (this.value instanceof NullJsonVerify) {
            return "null";
        }
        if (this.value instanceof NumberJsonVerify) {
            return "" + ((this.value as NumberJsonVerify).value);
        }
        if (this.value instanceof StringJsonVerify) {
            let stringJsonVerify = this.value as StringJsonVerify;
            return stringJsonVerify.value ? '"' + (stringJsonVerify.value) + '"' : '';
        }

        return "";
    }

    isEmptyValue(): boolean {
        if (this.isValueAContainer()) {
            return (this.value as JsonTreeContainer).getChildren().length == 0;
        }
        return false;
    }

    isValueAContainer(): boolean {
        return this.isValueAnObject() || this.isValueAnArray();
    }

    isValueAnObject(): boolean {
        return this.value instanceof ObjectJsonVerify;
    }

    isValueAnArray(): boolean {
        return this.value instanceof ArrayJsonVerify;
    }

    hasChildren(): boolean {
        if (this.isValueAContainer()) {
            return (this.value as JsonTreeContainer).getChildren().length != 0;
        }
        return false;
    }

    canDeserialize(input: any): boolean {
        let isArray = Array.isArray(input);
        if (!isArray) {
            return false;
        }
        let inputAsArray = input as Array<any>;

        return inputAsArray.length == 2 && typeof inputAsArray == "string";
    }

    //This will receive an Array with two properties, the key and the value
    deserialize(input: Object): FieldJsonVerify {
        let inputAsArray = input as Array<any>;

        this.name = inputAsArray[0];
        this.value = SerializationUtil.deserialize(inputAsArray[1]);
        if (this.isValueAContainer()) {
            this.compareMode = this.value.compareMode;
        }
        return this;
    }

    serialize(): string {
        let result = '"' + this.name + '":';

        if (this.isValueAContainer()) {
            this.value.compareMode = this.compareMode;
            return result + (this.value as Serializable<any>).serialize();
        }
        return result + JsonUtil.stringify(this.value.value);
    }
}
