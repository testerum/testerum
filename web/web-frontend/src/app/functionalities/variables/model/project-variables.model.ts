import {VariablesEnvironment} from "./variables-environment.model";
import {Serializable} from "../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {Variable} from "./variable.model";
import {VariablesService} from "../../../service/variables.service";

export class ProjectVariables implements Serializable<ProjectVariables> {
    public static DEFAULT_ENVIRONMENT_NAME = "default environment";
    public static LOCAL_ENVIRONMENT_NAME = "local environment";

    currentEnvironment: string;
    defaultVariables: Variable[] = [];
    localVariables: Variable[] = [];
    environments: VariablesEnvironment[] = [];

    getAllAvailableEnvironments(): string[] {
        var availableEnvironments: string[] = [];
        availableEnvironments.push(ProjectVariables.DEFAULT_ENVIRONMENT_NAME);
        if (this.localVariables.length > 0) {
            availableEnvironments.push(ProjectVariables.LOCAL_ENVIRONMENT_NAME);
        }

        for (const environment of this.environments) {
            availableEnvironments.push(environment.name);
        }
        return availableEnvironments;
    }

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

    getEnvironmentByName(environmentNameToEdit: string): VariablesEnvironment {
        for (const environment of this.environments) {
            if (environment.name == environmentNameToEdit) {
                return environment;
            }
        }
        return null;
    }

    sortEnvironmentVariablesByName() {
        this.environments.sort((a,b) => a.name > b.name ? 1 : -1);
    }
}
