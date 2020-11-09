import {StepDef} from "../../../../../../model/step/step-def.model";
import {StepSearchItem} from "../../../../../../utils/step-search/model/step-search-item.model";

export class StepCallSuggestion extends StepSearchItem {
    stepText: string;
    stepDef: StepDef;
    tokens: Array<string>;
    matchingPercentage: number = 0;
    isPerfectMatch: boolean = false;

    actionText: string;

    constructor(stepText: string,  stepDef: StepDef, actionText: string = null ) {
        super(stepText, stepDef);
        this.actionText = actionText;
    }
}
