
import {Variable} from "./variable.model";

export class VariableEvent {

    readonly variable: Variable;
    readonly eventType: VariableEventType;

    constructor(variable: Variable, eventType: VariableEventType) {
        this.variable = variable;
        this.eventType = eventType;
    }
}

export enum VariableEventType {
    ADDED,
    REMOVED,
    VALUE_CHANGED
}