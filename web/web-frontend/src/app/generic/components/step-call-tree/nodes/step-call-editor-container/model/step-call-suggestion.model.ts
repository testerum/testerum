import {StepPhaseEnum, StepPhaseUtil} from "../../../../../../model/enums/step-phase.enum";

export class StepCallSuggestion {
    phase: StepPhaseEnum;
    stepCallText: string;
    actionText: string;
    matchingPercentage: number;

    constructor(phase: StepPhaseEnum, stepCallText: string,  matchingPercentage: number = 0, actionText: string = null ) {
        this.phase = phase;
        this.stepCallText = stepCallText;
        this.matchingPercentage = matchingPercentage;
        this.actionText = actionText;
    }

    getPhaseAsString(): string {
        return StepPhaseUtil.toCamelCaseString(this.phase);
    }
}
