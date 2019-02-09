import {StepPhaseEnum, StepPhaseUtil} from "../../../../../../model/enums/step-phase.enum";
import {StepDef} from "../../../../../../model/step-def.model";

export class StepCallSuggestion {
    stepCallText: string;
    stepDef: StepDef;
    actionText: string;
    score: number;

    constructor(stepCallText: string,  stepDef: StepDef, actionText: string = null ) {
        this.stepCallText = stepCallText;
        this.stepDef = stepDef;
        this.actionText = actionText;
    }
}
