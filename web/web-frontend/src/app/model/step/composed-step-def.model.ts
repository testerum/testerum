import {StepDef} from "./step-def.model";
import {StepPhaseEnum} from "../enums/step-phase.enum";
import {StepCall} from "./step-call.model";
import {JsonUtil} from "../../utils/json.util";
import {IdUtils} from "../../utils/id.util";
import {StepPattern} from "../text/step-pattern.model";
import {Path} from "../infrastructure/path/path.model";
import {Warning} from "../warning/Warning";
import {StringUtils} from "../../utils/string-utils.util";
import {Serializable} from "../infrastructure/serializable.model";
import {UndefinedStepDef} from "./undefined-step-def.model";

export class ComposedStepDef implements StepDef, Serializable<ComposedStepDef> {

    id: string = IdUtils.getTemporaryId();
    path: Path;
    oldPath: Path;
    phase: StepPhaseEnum;
    stepPattern: StepPattern = new StepPattern;
    description: string;
    tags: Array<string> = [];

    stepCalls: Array<StepCall> = [];

    warnings: Array<Warning> = [];
    descendantsHaveWarnings: boolean = false;
    isUsed: boolean = true;

    get hasOwnOrDescendantWarnings(): boolean {
        return this.warnings.length > 0 || this.descendantsHaveWarnings;
    }

    addStepCall(stepCall:StepCall): void {
        this.stepCalls.push(stepCall);
        this.stepPattern.ownVariableHolders.addEventListener(
            stepCall.variableHolder
        );
    }

    deserialize(input: Object): ComposedStepDef {
        this.id = input["id"];
        this.path = Path.deserialize(input["path"]);
        this.oldPath = Path.deserialize(input["oldPath"]);
        this.phase = StepPhaseEnum["" + input["phase"]];
        this.stepPattern = new StepPattern().deserialize(input["stepPattern"]);
        this.description = input["description"];
        this.tags = input["tags"] || [];

        this.stepCalls = [];
        for (let stepCallJson of (input["stepCalls"] || [])) {
            let stepCall = new StepCall().deserialize(stepCallJson);
            if (stepCall.stepDef instanceof UndefinedStepDef) {
                stepCall.stepDef.path = this.path.getParentPath();
            }
            this.addStepCall(stepCall);
        }

        this.warnings = [];
        for (let warning of (input['warnings'] || [])) {
            this.warnings.push(
                new Warning().deserialize(warning)
            );
        }

        this.descendantsHaveWarnings = input['descendantsHaveWarnings'];
        this.isUsed = input['isUsed'];

        return this;
    }

    serialize(): string {
        return "" +
            '{' +
            '"@type": "COMPOSED_STEP",' +
            '"id":' + JsonUtil.stringify(this.id) + ',' +
            '"phase":' + JsonUtil.stringify(StepPhaseEnum[this.phase] ? StepPhaseEnum[this.phase].toUpperCase(): null) + ',' +
            '"path":' + JsonUtil.serializeSerializable(this.path) + ',' +
            '"oldPath":' + JsonUtil.serializeSerializable(this.oldPath) + ',' +
            '"stepPattern":' + this.stepPattern.serialize() + ',' +
            '"description":' + JsonUtil.stringify(this.description) + ',' +
            '"tags":' + JsonUtil.stringify(this.tags) + ',' +
            '"stepCalls":' + JsonUtil.serializeArrayOfSerializable(this.stepCalls) + ',' +
            '"warnings": []' + ',' +
            '"isUsed":' + JsonUtil.stringify(this.isUsed) +
            '}'
    }

    clone(): ComposedStepDef {
        let objectAsJson = JSON.parse(this.serialize());
        return new ComposedStepDef().deserialize(objectAsJson);
    }

    toString():string {
        return StringUtils.toTitleCase(StepPhaseEnum[this.phase]) + " " + this.stepPattern.getPatternText()
    }
}
