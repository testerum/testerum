import {StepPatternPart} from "./parts/step-pattern-part.model";
import {ParamStepPatternPart} from "./parts/param-step-pattern-part.model";
import {StepPatternParser} from "./parser/step-pattern-parser";
import {TextStepPatternPart} from "./parts/text-step-pattern-part.model";
import {JsonUtil} from "../../utils/json.util";
import {VariableEvent, VariableEventType} from "../../generic/variable/variable-event.model";
import {Variable} from "../../generic/variable/variable.model";
import {VariableHolder} from "../../generic/variable/variable-holder.model";

export class StepPattern implements Serializable<StepPattern> {

    patternParts: Array<StepPatternPart> = [];

    ownVariableHolders: VariableHolder = new VariableHolder();

    setPatternText(text: string) {
        this.patternParts.length = 0;
        this.patternParts = this.patternParts.concat (
            StepPatternParser.parsePatternText(text)
        );

        this.calculateAndPushVariablesForChildren(
            this.getParamParts()
        )
    }

    getParamParts(): Array<ParamStepPatternPart> {
        return this.patternParts
            .filter((value) => value instanceof ParamStepPatternPart)
            .map(value => value as ParamStepPatternPart);
    }

     getPatternText(): string {
        let result = "";
        for (let patternPart of this.patternParts) {
            if (patternPart instanceof TextStepPatternPart) {
                result += patternPart.text;
            } else if (patternPart instanceof ParamStepPatternPart) {
                result += "<<" + patternPart.name + ">>"
            } else {
                throw new Error("Unrecognized StepPatternPart")
            }
        }
        return result;
    }

    isParamPart(patternPart:StepPatternParser) {
        return patternPart instanceof ParamStepPatternPart;
    }

    deserialize(input: Object): StepPattern {
        if(!input) {
            return this;
        }

        for (let patternPart of input["patternParts"] || []) {
            if (patternPart["@type"] == "TEXT") {
                this.patternParts.push(new TextStepPatternPart().deserialize(patternPart));
            }
            if (patternPart["@type"] == "PARAM") {
                let paramPart = new ParamStepPatternPart().deserialize(patternPart);
                this.patternParts.push(paramPart);
                this.handleCreatedVariable(paramPart)
            }
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"patternParts":' + JsonUtil.serializeArrayOfSerializable(this.patternParts) +
            '}'
    }


    private calculateAndPushVariablesForChildren(newParamPatternParts: Array<ParamStepPatternPart>): void {
        this.handleDeletedVariables(newParamPatternParts);
        this.handleCreatedVariables(newParamPatternParts)
    }

    private handleDeletedVariables(newParamPatternParts: Array<ParamStepPatternPart>): void {
        for (let ownVariable of this.ownVariableHolders.variables) {
            let found = false;
            for (let newParamPart of newParamPatternParts) {
                if (newParamPart.name === ownVariable.name) {
                    found = true;
                }
            }
            if (!found) {
                let deletedVariableEvent = new VariableEvent(ownVariable, VariableEventType.REMOVED);
                this.ownVariableHolders.onVariableChange(deletedVariableEvent);
            }
        }
    }

    private handleCreatedVariables(newParamPatternParts: Array<ParamStepPatternPart>): void {
        for (let newParamPart of newParamPatternParts) {
            this.handleCreatedVariable(newParamPart);
        }
    }

    private handleCreatedVariable(paramPart: ParamStepPatternPart): void {
        let found = false;
        for (let ownVariable of this.ownVariableHolders.variables) {
            if (paramPart.name === ownVariable.name) {
                found = true;
            }
        }
        if (!found) {
            let newVariable = new Variable();
            newVariable.type = paramPart.uiType;
            newVariable.name = paramPart.name;

            let deletedVariableEvent = new VariableEvent(newVariable, VariableEventType.ADDED);
            this.ownVariableHolders.onVariableChange(deletedVariableEvent);
        }
    }

    getInvalidParam(variables:Array<Variable>): ParamStepPatternPart {
        for (let paramPart of this.getParamParts()) {
            let variableByName = this.getVariableByName(paramPart.name, variables);
            if(variableByName == null) {
                return paramPart;
            }
        }

        return null;
    }

    private getVariableByName(name: string, variables:Array<Variable>): Variable {
        for (let variable of variables) {
            if(variable.name === name) {
                return variable;
            }
        }

        return null;
    }
}
