
import {Variable} from "./variable.model";
import {VariableEventListener} from "./variable-event.listener";
import {VariableEvent, VariableEventType} from "./variable-event.model";
import {variable} from "@angular/compiler/src/output/output_ast";

export class VariableHolder implements VariableEventListener {

    variables:Array<Variable> = [];

    private listeners: Array<VariableEventListener> = [];

    initializeVariables(variables: Array<Variable>): void {
        variables.forEach(variable => this.variables.push(variable));
    }

    addEventListener(listener: VariableEventListener): void {
        listener.initializeVariables(this.variables);
        this.listeners.push(listener)
    }

    onVariableChange(variableEvent: VariableEvent): void {
        if(variableEvent.eventType == VariableEventType.ADDED) {
            this.variables.push(variableEvent.variable);
        }

        if(variableEvent.eventType == VariableEventType.REMOVED) {
            let index: number = this.variables.indexOf(variableEvent.variable);
            if (index !== -1) {
                this.variables.splice(index, 1);
            }
        }

        this.triggerEventChange(variableEvent)
    }

    private triggerEventChange(variableEvent: VariableEvent) {
        for (let listener of this.listeners) {
            listener.onVariableChange(variableEvent);
        }
    }

    getVariableByName(name: string): Variable {
        for (let variable of this.variables) {
            if(variable.name === name) {
                return variable;
            }
        }

        return null;
    }
}