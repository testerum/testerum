import {StepDef} from "./step-def.model";
import {JsonUtil} from "../utils/json.util";
import {BasicStepDef} from "./basic-step-def.model";
import {Arg} from "./arg/arg.model";
import {StepPhaseEnum} from "./enums/step-phase.enum";
import {StringUtils} from "../utils/string-utils.util";
import {IdUtils} from "../utils/id.util";
import {ComposedStepDef} from "./composed-step-def.model";
import {VariableHolder} from "../generic/variable/variable-holder.model";
import {TextStepPatternPart} from "./text/parts/text-step-pattern-part.model";
import {ParamStepPatternPart} from "./text/parts/param-step-pattern-part.model";
import {UndefinedStepDef} from "./undefined-step-def.model";
import {Warning} from "./warning/Warning";
import {ArrayUtil} from "../utils/array.util";

export class StepCall implements Serializable<StepCall> {

    id: string = IdUtils.getTemporaryId();
    stepDef: StepDef;
    args: Array<Arg> = [];
    variableHolder: VariableHolder = new VariableHolder();

    private warnings: Array<Warning> = [];
    private descendantsHaveWarnings: boolean = false;

    private allWarnings;
    getAllWarnings(): Array<Warning> {
        if (this.allWarnings != null) {
            return this.allWarnings;
        }

        this.allWarnings = [];
        this.warnings.forEach(it => this.allWarnings.push(it));
        if (this.stepDef instanceof ComposedStepDef) {
            (this.stepDef as ComposedStepDef).warnings.forEach(it => this.allWarnings.push(it));
        }
        return this.allWarnings;
    }

    addWarning(warning: Warning) {
        this.warnings.push(warning)
    }

    getAnyDescendantsHaveWarnings(): boolean {
        if(this.descendantsHaveWarnings) return true;
        if (this.stepDef instanceof ComposedStepDef) {
            return (this.stepDef as ComposedStepDef).descendantsHaveWarnings;
        }
        return false;
    }

    deserialize(input: Object): StepCall {
        this.id = input["id"];
        if (input["stepDef"]["@type"] == "BASIC_STEP") {
            this.stepDef = new BasicStepDef().deserialize(input["stepDef"]);
        }
        if (input["stepDef"]["@type"] == "COMPOSED_STEP") {
            this.stepDef = new ComposedStepDef().deserialize(input["stepDef"]);
        }
        if (input["stepDef"]["@type"] == "UNDEFINED_STEP") {
            this.stepDef = new UndefinedStepDef().deserialize(input["stepDef"]);
        }

        this.args = [];
        for (let argJson of (input["args"] || [])) {
            this.args.push(new Arg().deserialize(argJson));
        }

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
        return "" +
            '{' +
            '"id":' + JsonUtil.stringify(this.id) + ',' +
            '"stepDef":' + this.stepDef.serialize() + ',' +
            '"args":' + JsonUtil.serializeArrayOfSerializable(this.args) + ',' +
            '"warnings": []' +
            '}'
    }

    getTextWithParamValues(previewsPhase: StepPhaseEnum): string {
        let paramIndex = 0;

        let phaseAsString: string = StepPhaseEnum[this.stepDef.phase];
        if (this.stepDef.phase == previewsPhase) {
            phaseAsString = "And"
        }
        let stepText = StringUtils.toTitleCase(phaseAsString) + " ";

        for (let patternPart of this.stepDef.stepPattern.patternParts) {
            if (patternPart instanceof TextStepPatternPart) {
                stepText += patternPart.text;
            }
            if (patternPart instanceof ParamStepPatternPart) {
                let argName = this.args[paramIndex].name;
                stepText += argName;
                paramIndex++;
            }
        }
        return stepText;
    }

    getStepPatternParamByIndex(index: number): ParamStepPatternPart {
        let paramParts = this.stepDef.stepPattern.getParamParts();
        if (paramParts.length <= index) {
            return null;
        }
        return paramParts[index];
    }
}
