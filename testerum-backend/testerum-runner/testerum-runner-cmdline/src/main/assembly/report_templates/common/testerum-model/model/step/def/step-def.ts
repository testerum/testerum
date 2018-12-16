import {Path} from "../../path";
import {StepPhaseEnum} from "./step-phase-enum";
import {StepPattern} from "./pattern/step-pattern";

export interface StepDef {
    id: string;
    path: Path;
    phase: StepPhaseEnum;
    stepPattern: StepPattern;
    description: string|null;
    tags: Array<string>;
}

export enum StepDefType {
    BASIC_STEP,
    COMPOSED_STEP,
    UNDEFINED_STEP,
}
