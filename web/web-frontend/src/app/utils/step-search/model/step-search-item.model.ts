import {StepSearchTokenizationUtil} from "../util/step-search-tokenization.util";
import {StepDef} from "../../../model/step-def.model";

export class StepSearchItem {
    stepText: string;
    stepDef: StepDef;
    tokens: Array<string>;
    matchingPercentage: number = 0;
    isPerfectMatch: boolean = false;

    constructor(stepText: string, stepDef: StepDef) {
        this.stepText = stepText;
        this.stepDef = stepDef;
        this.tokens = StepSearchTokenizationUtil.tokenize(stepText);
    }
}
