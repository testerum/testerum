
import {StepDef} from "../../../model/step-def.model";

export interface StepChoseHandler {
    onStepChose(choseStep: StepDef):void;
}