import {StepDef} from "./step-def.model";
import {IdUtils} from "../utils/id.util";
import {StepPhaseEnum} from "./enums/step-phase.enum";
import {Path} from "./infrastructure/path/path.model";
import {StepPattern} from "./text/step-pattern.model";
import {JsonUtil} from "../utils/json.util";
import {Warning} from "./warning/Warning";
import {Serializable} from "./infrastructure/serializable.model";

export class UndefinedStepDef implements StepDef, Serializable<UndefinedStepDef> {

    id: string = IdUtils.getTemporaryId();
    path: Path = null;
    phase: StepPhaseEnum;
    stepPattern: StepPattern = new StepPattern;
    description: string;

    warnings: Array<Warning> = [];
    descendantsHaveWarnings: boolean = false;

    get hasOwnOrDescendantWarnings(): boolean {
        return this.warnings.length > 0 || this.descendantsHaveWarnings;
    }

    deserialize(input: Object): UndefinedStepDef {
        this.id = input["id"];
        this.path = null;
        this.phase = StepPhaseEnum["" + input["phase"]];
        this.stepPattern = new StepPattern().deserialize(input["stepPattern"]);
        this.description = input["description"];

        this.warnings = [];
        for (let warning of (input['warnings'] || [])) {
            this.warnings.push(
                new Warning().deserialize(warning)
            );
        }

        this.descendantsHaveWarnings = input['descendantsHaveWarnings'];

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
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"warnings": []' +
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
