import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";

export interface ReportStepDef {
    readonly id: string;
    readonly phase: StepPhaseEnum;
    readonly stepPattern: StepPattern;
}

export enum StepDefType {
    BASIC_STEP,
    COMPOSED_STEP,
    UNDEFINED_STEP,
}
