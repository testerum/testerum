import {ParamStepPatternPart} from "../../../../../../common/testerum-model/model/step/def/pattern/part/param-step-pattern-part";
import {TextStepPatternPart} from "../../../../../../common/testerum-model/model/step/def/pattern/part/text-step-pattern-part";
import {StepPhaseEnum} from "../../../../../../common/testerum-model/model/step/def/step-phase-enum";
import {HtmlUtil} from "./html.util";
import {ReportStepCall} from "../../../../../../common/testerum-model/model/step/call/report-step-call";
import {ReportStepCallArg} from "../../../../../../common/testerum-model/model/step/call/report-step-call-arg";
import {ReportStepDef} from "../../../../../../common/testerum-model/model/step/def/report-step-def";

export class StepCallUtil {
    static getStepCallAsHtmlText(stepCall: ReportStepCall, reportStepDef: ReportStepDef): string {
        let result = '<div class="step-call">';
        result += StepCallUtil.getStepCallPhase(stepCall, reportStepDef) + ' ';
        result += StepCallUtil.getStepCallPattern(stepCall, reportStepDef);
        result +='</div>';
        return result;
    }

    private static getStepCallPhase(stepCall: ReportStepCall, reportStepDef: ReportStepDef): string {
        return '<span class="step-phase">'+StepPhaseEnum[reportStepDef.phase]+'</span>';
    }

    private static getStepCallPattern(stepCall: ReportStepCall, reportStepDef: ReportStepDef) {
        let result = '';
        let paramIndex = 0;
        for (const patternPart of reportStepDef.stepPattern.patternParts) {
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

    private static getParamNameFromArg(stepCall: ReportStepCall, paramIndex: number) {
        if (stepCall.args.length <= paramIndex) {
            return null;
        }

        let arg: ReportStepCallArg = stepCall.args[paramIndex];

        if (arg.name) {
            return arg.name;
        }

        return arg.content;
    }
}
