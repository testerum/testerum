import {StepCall} from "../../../../../../common/testerum-model/model/step/call/step-call";
import {ParamStepPatternPart} from "../../../../../../common/testerum-model/model/step/def/pattern/part/param-step-pattern-part";
import {TextStepPatternPart} from "../../../../../../common/testerum-model/model/step/def/pattern/part/text-step-pattern-part";
import {Arg} from "../../../../../../common/testerum-model/model/step/call/arg";
import {StepPhaseEnum} from "../../../../../../common/testerum-model/model/step/def/step-phase-enum";
import {HtmlUtil} from "./html.util";

export class StepCallUtil {
    static getStepCallAsHtmlText(stepCall: StepCall): string {
        let result = '<div class="step-call">';
        result += StepCallUtil.getStepCallPhase(stepCall) + ' ';
        result += StepCallUtil.getStepCallPattern(stepCall);
        result +='</div>';
        return result;
    }

    private static getStepCallPhase(stepCall: StepCall): string {
        return '<span class="step-phase">'+StepPhaseEnum[stepCall.stepDef.phase]+'</span>';
    }

    private static getStepCallPattern(stepCall: StepCall) {
        let result = '';
        let paramIndex = 0;
        for (const patternPart of stepCall.stepDef.stepPattern.patternParts) {
            if (patternPart instanceof TextStepPatternPart) {
                result += '<span class="step-text-part">'+HtmlUtil.escapeHtml(patternPart.text)+'</span>';
            }

            if(patternPart instanceof ParamStepPatternPart) {
                let paramValue = StepCallUtil.getParamNameFromArg(stepCall, paramIndex);
                if (paramValue == null) {
                    paramValue = patternPart.name;
                }
                result += '<span class="step-param-part">'+HtmlUtil.escapeHtml("<<"+paramValue+">>")+'</span>';
                paramIndex++;
            }
        }
        return result;
    }

    private static getParamNameFromArg(stepCall: StepCall, paramIndex: number) {
        if (stepCall.args.length <= paramIndex) {
            return null;
        }

        let arg: Arg = stepCall.args[paramIndex];

        if (arg.name) {
            return arg.name;
        }

        return arg.content;
    }
}
