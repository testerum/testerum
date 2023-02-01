import {StepPhaseEnum, StepPhaseUtil} from "../../../../../../model/enums/step-phase.enum";

export class StepTextUtil {

    public static getStepPhaseFromStepText(stepText: string): StepPhaseEnum {

        let phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.GIVEN) + " ";
        let stepTextLowerCased = stepText.toLowerCase();
        if (stepTextLowerCased.startsWith(phaseAsString)) {
            return StepPhaseEnum.GIVEN;
        }

        phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.WHEN) + " ";
        if (stepTextLowerCased.startsWith(phaseAsString)) {
            return StepPhaseEnum.WHEN;
        }

        phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.THEN) + " ";
        if (stepTextLowerCased.startsWith(phaseAsString)) {
            return StepPhaseEnum.THEN;
        }

        phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.AND) + " ";
        if (stepTextLowerCased.startsWith(phaseAsString)) {
            return StepPhaseEnum.AND;
        }

        return null;
    }

    public static getStepTextWithoutStepPhase(fullStepText: string): string {
        let stepPhase = StepTextUtil.getStepPhaseFromStepText(fullStepText);
        if (stepPhase == null) {
            return fullStepText;
        }

        return fullStepText.substring(StepPhaseUtil.toLowerCaseString(stepPhase).length + 1)
    }

    public static isStepTextStartsWithAnd(stepText: string): boolean {
        let phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.AND) + " ";
        return stepText.toLowerCase().startsWith(phaseAsString);
    }
}
