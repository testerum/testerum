import {VariablesEnvironment} from "./variables-environment.model";
import {Serializable} from "../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {Variable} from "./variable.model";

export class ProjectVariables implements Serializable<ProjectVariables> {
    currentEnvironment: string;
    defaultVariables: Variable[] = [];
    localVariables: Variable[] = [];
    environments: VariablesEnvironment[] = [];

    deserialize(input: Object): ProjectVariables {
        this.currentEnvironment = input["currentEnvironment"];

        this.defaultVariables = [];
        for (let defaultVariableJson of (input['defaultVariables'] || [])) {
            this.defaultVariables.push(new Variable().deserialize(defaultVariableJson));
        }

        this.localVariables = [];
        for (let localVariableJson of (input['localVariables'] || [])) {
            this.localVariables.push(new Variable().deserialize(localVariableJson));
        }

        this.environments = [];
        for (let environmentJson of (input['environments'] || [])) {
            this.environments.push(new VariablesEnvironment().deserialize(environmentJson));
        }

        return this;
    }

    serialize(): string {
        return ""+
            '{' +
            ' "currentEnvironment":'+JsonUtil.stringify(this.currentEnvironment) +
            ',"defaultVariables":'+JsonUtil.serializeArrayOfSerializable(this.defaultVariables) +
            ',"localVariables":'+JsonUtil.serializeArrayOfSerializable(this.localVariables) +
            ',"environments":'+JsonUtil.serializeArrayOfSerializable(this.environments) +
            '}'
    }
}
