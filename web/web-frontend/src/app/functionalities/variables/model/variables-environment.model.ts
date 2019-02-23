import {Variable} from "./variable.model";
import {Serializable} from "../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";

export class VariablesEnvironment implements Serializable<VariablesEnvironment> {
    name: string;
    variables: Variable[] = [];

    deserialize(input: Object): VariablesEnvironment {
        this.name = input["name"];
        this.variables = [];
        for (let variableAsJson of (input["variables"] || [])) {
            this.variables.push(new Variable().deserialize(variableAsJson));
        }
        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            ' "name":' + JsonUtil.stringify(this.name) +
            ',"variables":' + JsonUtil.serializeArrayOfSerializable(this.variables.filter(it => {return !it.isEmpty()})) +
            '}'
    }
}
