export class StepContext {
    isPartOfManualTest: boolean = false;

    constructor(isPartOfManualTest: boolean = false) {
        this.isPartOfManualTest = isPartOfManualTest;
    }
}
