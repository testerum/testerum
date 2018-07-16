import {StepPhaseEnum, StepPhaseUtil} from "../../../../../../model/enums/step-phase.enum";

export class StepTextUtil {

    public static getStepPhaseFromStepText(stepText: string): StepPhaseEnum {

        let phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.GIVEN) + " ";
        if (stepText.toLowerCase().startsWith(phaseAsString)) {
            return StepPhaseEnum.GIVEN;
        }

        phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.WHEN) + " ";
        if (stepText.toLowerCase().startsWith(phaseAsString)) {
            return StepPhaseEnum.WHEN;
        }

        phaseAsString = StepPhaseUtil.toLowerCaseString(StepPhaseEnum.THEN) + " ";
        if (stepText.toLowerCase().startsWith(phaseAsString)) {
            return StepPhaseEnum.THEN;
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
}
