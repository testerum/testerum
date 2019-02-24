import {VariablesEnvironment} from "./variables-environment.model";
import {Serializable} from "../../../model/infrastructure/serializable.model";
import {JsonUtil} from "../../../utils/json.util";
import {Variable} from "./variable.model";
import {ArrayUtil} from "../../../utils/array.util";

export class AllProjectVariables implements Serializable<AllProjectVariables> {
    public static DEFAULT_ENVIRONMENT_NAME = "Default";
    public static LOCAL_ENVIRONMENT_NAME = "Local";

    currentEnvironment: string;
    defaultVariables: Variable[] = [];
    localVariables: Variable[] = [];
    environments: VariablesEnvironment[] = [];

    getAllAvailableEnvironments(): string[] {
        var availableEnvironments: string[] = [];
        availableEnvironments.push(AllProjectVariables.DEFAULT_ENVIRONMENT_NAME);
        availableEnvironments.push(AllProjectVariables.LOCAL_ENVIRONMENT_NAME);

        for (const environment of this.environments) {
            availableEnvironments.push(environment.name);
        }
        return availableEnvironments;
    }

    deserialize(input: Object): AllProjectVariables {
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
            ',"defaultVariables":'+JsonUtil.serializeArrayOfSerializable(this.defaultVariables.filter(it => {return !it.isEmpty()})) +
            ',"localVariables":'+JsonUtil.serializeArrayOfSerializable(this.localVariables.filter(it => {return !it.isEmpty()})) +
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

    getVariablesByEnvironmentName(selectedEnvironmentName: string): Variable[] {
        if (selectedEnvironmentName == AllProjectVariables.DEFAULT_ENVIRONMENT_NAME) {
            return this.defaultVariables;
        }
        if (selectedEnvironmentName == AllProjectVariables.LOCAL_ENVIRONMENT_NAME) {
            return this.localVariables;
        }
        for (const environment of this.environments) {
            if (environment.name == selectedEnvironmentName) {
                return environment.variables;
            }
        }
        return null
    }

    getVariablesByEnvironmentNameMergedWithDefaultVars(selectedEnvironmentName: string): Variable[] {
        if (selectedEnvironmentName == AllProjectVariables.DEFAULT_ENVIRONMENT_NAME) {
            return this.defaultVariables;
        }
        if (selectedEnvironmentName == AllProjectVariables.LOCAL_ENVIRONMENT_NAME) {
            return this.getVariablesMergedWithDefaultEnvironment(this.localVariables, this.defaultVariables);
        }
        for (const environment of this.environments) {
            if (environment.name == selectedEnvironmentName) {
                return this.getVariablesMergedWithDefaultEnvironment(environment.variables, this.defaultVariables);
            }
        }

        return ArrayUtil.copyArray(this.defaultVariables);
    }

    private getVariablesMergedWithDefaultEnvironment(environmentVariables: Variable[], defaultVariables: Variable[]): Variable[] {
        let result: Variable[] = [];

        for (const environmentVariable of environmentVariables) {
            if(environmentVariable.isEmpty()) continue;

            result.push(environmentVariable);
        }

        for (const defaultVariable of defaultVariables) {
            if(defaultVariable.isEmpty()) continue;

            if (!this.isVariableInList(environmentVariables, defaultVariable)) {
                result.push(
                    new Variable(defaultVariable.key, defaultVariable.value, true)
                )
            }
        }

        return result;
    }

    private isVariableInList(environmentVariables: Variable[], defaultVariable: Variable): boolean {
        for (const environmentVariable of environmentVariables) {
            if(environmentVariable.key != null && defaultVariable.key != null && environmentVariable.key.toUpperCase() == defaultVariable.key.toUpperCase()) {
                return true;
            }
        }
        return false;
    }

    setVariablesToEnvironment(environmentName: string, variables: Variable[]) {
        if (environmentName == AllProjectVariables.DEFAULT_ENVIRONMENT_NAME) {
            this.defaultVariables.length = 0;
            for (const variable of variables) {
                this.defaultVariables.push(variable)
            }
        }

        if (environmentName == AllProjectVariables.LOCAL_ENVIRONMENT_NAME) {
            this.localVariables.length = 0;
            for (const variable of variables) {
                this.localVariables.push(variable)
            }
        }

        for (const environment of this.environments) {
            if (environment.name == environmentName) {
                environment.variables.length = 0;
                for (const variable of variables) {
                    environment.variables.push(variable)
                }
            }
        }
    }
}