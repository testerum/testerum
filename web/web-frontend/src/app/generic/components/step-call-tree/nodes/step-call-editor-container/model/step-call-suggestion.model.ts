
export class StepCallSuggestion {
    stepCallText: string;
    actionText: string;

    constructor(stepCallText: string, actionText: string = null ) {
        this.stepCallText = stepCallText;
        this.actionText = actionText;
    }
}
