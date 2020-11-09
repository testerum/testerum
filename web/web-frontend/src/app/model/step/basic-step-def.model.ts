import {StepDef} from "./step-def.model";
import {JsonUtil} from "../../utils/json.util";
import {IdUtils} from "../../utils/id.util";
import {StepPhaseEnum} from "../enums/step-phase.enum";
import {StepPattern} from "../text/step-pattern.model";
import {Path} from "../infrastructure/path/path.model";
import {Warning} from "../warning/Warning";
import {Serializable} from "../infrastructure/serializable.model";

export class BasicStepDef implements Serializable<BasicStepDef>, StepDef {
    id:string = IdUtils.getTemporaryId();
    path:Path;
    phase: StepPhaseEnum;
    stepPattern:StepPattern = new StepPattern();
    description: string;
    tags: Array<string> = [];

    className: string;
    methodName: string;

    warnings: Array<Warning> = [];
    descendantsHaveWarnings: boolean = false;

    get hasOwnOrDescendantWarnings(): boolean {
        return this.warnings.length > 0 || this.descendantsHaveWarnings;
    }

    deserialize(input: Object): BasicStepDef {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.phase = StepPhaseEnum[""+input["phase"]];
        this.stepPattern = new StepPattern().deserialize(input["stepPattern"]);
        this.description = input["description"];
        this.tags = input["tags"];
        this.className = input["className"];
        this.methodName = input["methodName"];

        this.warnings = [];
        for (let warning of (input['warnings'] || [])) {
            this.warnings.push(
                new Warning().deserialize(warning)
            );
        }

        this.descendantsHaveWarnings = input['descendantsHaveWarnings'];

        return this;
    }

    serialize() {
        return ""+
            '{' +
            '"@type": "BASIC_STEP",'+
            '"id":'+ JsonUtil.stringify(this.id)+','+
            '"path":'+ JsonUtil.serializeSerializable(this.path)+','+
            '"phase":'+ JsonUtil.stringify(StepPhaseEnum[this.phase].toUpperCase())+','+
            '"stepPattern":' + this.stepPattern.serialize() + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"tags":' + JsonUtil.stringify(this.tags) + ',' +
            '"className":' + JsonUtil.stringify(this.className) + ',' +
            '"methodName":' + JsonUtil.stringify(this.methodName) + ',' +
            '"warnings": []' +
            '}'
    }

    clone(): BasicStepDef {
        let objectAsJson = JSON.parse(this.serialize());
        return new BasicStepDef().deserialize(objectAsJson);
    }

    toString():string {
        return this.phase + " " + this.stepPattern.getPatternText()
    }
}
