
import {StepDef} from "./step-def.model";
import {ComposedStepDef} from "./composed-step-def.model";
import {IdUtils} from "../utils/id.util";
import {StepPhaseEnum} from "./enums/step-phase.enum";
import {Path} from "./infrastructure/path/path.model";
import {StepPattern} from "./text/step-pattern.model";
import {JsonUtil} from "../utils/json.util";

export class UndefinedStepDef implements StepDef, Serializable<UndefinedStepDef> {

    id: string = IdUtils.getTemporaryId();
    path: Path = null;
    phase: StepPhaseEnum;
    stepPattern: StepPattern = new StepPattern;
    description: string;

    deserialize(input: Object): UndefinedStepDef {
        this.id = input["id"];
        this.path = null;
        this.phase = StepPhaseEnum["" + input["phase"]];
        this.stepPattern = new StepPattern().deserialize(input["stepPattern"]);
        this.description = input["description"];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"@type": "UNDEFINED_STEP",' +
            '"id":'+ JsonUtil.stringify(this.id)+','+
            '"phase":' + JsonUtil.stringify(StepPhaseEnum[this.phase].toUpperCase()) + ',' +
            '"path":'+ JsonUtil.serializeSerializable(this.path)+','+
            '"stepPattern":' + this.stepPattern.serialize() +','+
            '"description":' + JsonUtil.stringify(this.description) +
            '}'
    }

    clone(): UndefinedStepDef {
        let objectAsJson = JSON.parse(this.serialize());
        return new UndefinedStepDef().deserialize(objectAsJson);
    }

    toString():string {
        return this.phase + " " + this.stepPattern.getPatternText()
    }
}
