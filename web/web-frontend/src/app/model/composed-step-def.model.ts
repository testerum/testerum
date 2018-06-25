import {StepDef} from "./step-def.model";
import {StepPhaseEnum} from "./enums/step-phase.enum";
import {StepCall} from "./step-call.model";
import {JsonUtil} from "../utils/json.util";
import {IdUtils} from "../utils/id.util";
import {VariableEvent, VariableEventType} from "../generic/variable/variable-event.model";
import {VariableHolder} from "../generic/variable/variable-holder.model";
import {Variable} from "../generic/variable/variable.model";
import {ParamStepPatternPart} from "./text/parts/param-step-pattern-part.model";
import {TextStepPatternPart} from "./text/parts/text-step-pattern-part.model";
import {StepPattern} from "./text/step-pattern.model";
import {Path} from "./infrastructure/path/path.model";

export class ComposedStepDef implements StepDef, Serializable<ComposedStepDef> {

    id: string = IdUtils.getTemporaryId();
    path: Path;
    phase: StepPhaseEnum;
    stepPattern: StepPattern = new StepPattern;
    description: string;

    stepCalls: Array<StepCall> = [];

    addStepCall(stepCall:StepCall): void {
        this.stepCalls.push(stepCall);
        this.stepPattern.ownVariableHolders.addEventListener(
            stepCall.variableHolder
        );
    }

    deserialize(input: Object): ComposedStepDef {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.phase = StepPhaseEnum["" + input["phase"]];
        this.stepPattern = new StepPattern().deserialize(input["stepPattern"]);
        this.description = input["description"];

        for (let stepCallAsJson of input["stepCalls"]) {
            let stepCall = new StepCall().deserialize(stepCallAsJson);
            this.addStepCall(stepCall)
        }

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"@type": "COMPOSED_STEP",' +
            '"id":' + JsonUtil.stringify(this.id) + ',' +
            '"phase":' + JsonUtil.stringify(StepPhaseEnum[this.phase].toUpperCase()) + ',' +
            '"path":' + JsonUtil.serializeSerializable(this.path) + ',' +
            '"stepPattern":' + this.stepPattern.serialize() + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"stepCalls":' + JsonUtil.serializeArrayOfSerializable(this.stepCalls) +
            '}'
    }

    clone(): ComposedStepDef {
        let objectAsJson = JSON.parse(this.serialize());
        return new ComposedStepDef().deserialize(objectAsJson);
    }

    toString():string {
        return this.phase + " " + this.stepPattern.getPatternText()
    }
}
