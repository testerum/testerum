import {StepDef} from "./step-def.model";
import {JsonUtil} from "../../utils/json.util";
import {BasicStepDef} from "./basic-step-def.model";
import {Arg} from "../arg/arg.model";
import {StepPhaseEnum} from "../enums/step-phase.enum";
import {StringUtils} from "../../utils/string-utils.util";
import {IdUtils} from "../../utils/id.util";
import {ComposedStepDef} from "./composed-step-def.model";
import {VariableHolder} from "../../generic/variable/variable-holder.model";
import {TextStepPatternPart} from "../text/parts/text-step-pattern-part.model";
import {ParamStepPatternPart} from "../text/parts/param-step-pattern-part.model";
import {UndefinedStepDef} from "./undefined-step-def.model";
import {Warning} from "../warning/Warning";
import {Serializable} from "../infrastructure/serializable.model";
import {ArrayUtil} from "../../utils/array.util";
import {StepTextParamUtil} from "../../generic/components/step-text/step-text-param/util/step-text-param.util";

export class StepCall implements Serializable<StepCall> {

    id: string = IdUtils.getTemporaryId();
    stepDef: StepDef;
    args: Array<Arg> = [];
    enabled: boolean = true;

    variableHolder: VariableHolder = new VariableHolder();

    private warnings: Array<Warning> = [];
    descendantsHaveWarnings: boolean = false;

    get hasOwnOrDescendantWarnings(): boolean {
        return this.warnings.length > 0 || this.descendantsHaveWarnings;
    }

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

    getWarnings(): Warning[] {
        return this.warnings;
    }

    addWarning(warning: Warning) {
        this.warnings.push(warning)
    }

    setWarnings(warnings: Warning[]) {
        this.allWarnings = null;
        ArrayUtil.replaceElementsInArray(this.warnings, warnings);
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
        for (let i = 0; i <(input["args"] || []).length; i++) {
            let argJson = (input["args"] || [])[i];
            let arg = new Arg().deserialize(argJson);

            let paramPart = this.stepDef.stepPattern.getParamParts()[i];
            arg.paramName = paramPart.name;

            this.args.push(arg);
        }

        this.enabled = input['enabled'];

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
            '"enabled":' + JsonUtil.stringify(this.enabled) + ',' +
            '"warnings": []' +
            '}'
    }

    clone(): StepCall {
        return new StepCall().deserialize(JSON.parse(this.serialize()));
    }

    getTextWithParamValues(previousPhase: StepPhaseEnum, escapeHtml: boolean = false): string {
        let paramIndex = 0;

        let phaseAsString: string = StepPhaseEnum[this.stepDef.phase];
        if (this.stepDef.phase == previousPhase) {
            phaseAsString = "And"
        }
        let stepText = StringUtils.toTitleCase(phaseAsString) + " ";

        for (let patternPart of this.stepDef.stepPattern.patternParts) {
            if (patternPart instanceof TextStepPatternPart) {
                stepText += patternPart.text;
            }
            if (patternPart instanceof ParamStepPatternPart) {
                let currentArg = this.args[paramIndex];
                if (StepTextParamUtil.hasValue(currentArg)) {
                    stepText += (escapeHtml?"&lt;&lt;":"<<") + StepTextParamUtil.getArgumentValue(currentArg) + (escapeHtml?"&gt;&gt;":">>")
                } else {
                    stepText += (escapeHtml?"&lt;&lt;":"<<") + StepTextParamUtil.getArgumentName(currentArg, patternPart) + (escapeHtml?"&gt;&gt;":">>")
                }
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
