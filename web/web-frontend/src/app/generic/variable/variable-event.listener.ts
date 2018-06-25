
import {VariableEvent} from "./variable-event.model";
import {Variable} from "./variable.model";

export interface VariableEventListener {
    initializeVariables(variables: Array<Variable>): void;
    onVariableChange(variableEvent: VariableEvent): void;
}