import {JsonUtil} from "../../../utils/json.util";
import {Serializable} from "../../../model/infrastructure/serializable.model";

export class Variable implements Serializable<Variable> {
    key: string;
    value: string;

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
        response += ',"value":' + JsonUtil.stringify(this.value);
        response += '}';
        return response;
    }

    isEmpty(): boolean {
        return !(this.key || this.value);
    }
}
