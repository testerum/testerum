import {JsonUtil} from "../../../utils/json.util";
import {Serializable} from "../../../model/infrastructure/serializable.model";

export class Variable implements Serializable<Variable> {
    key: string;
    value: string = "";
    isVariableFromDefaultEnvironment: boolean = false;

    constructor(key: string = null, value: string = "", isVariableFromDefaultEnvironment: boolean = false) {
        this.key = key;
        this.value = value;
        this.isVariableFromDefaultEnvironment = isVariableFromDefaultEnvironment;
    }

    deserialize(input: Object): Variable {
        this.key = input["key"];
        this.value = input["value"];
        return this;
    }

    serialize(): string {
        if(!this.key && !this.value) {
            return "";
        }

        let response = '{';
        response += '"key":' + JsonUtil.stringify(this.key);
        response += ',"value":' + (this.value ? JsonUtil.stringify(this.value) : JsonUtil.stringify(""));
        response += '}';
        return response;
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }

    hasEmptyValue(): boolean {
        return this.value == null || this.value == "";
    }
}
