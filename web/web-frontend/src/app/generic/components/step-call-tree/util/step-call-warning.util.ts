import {StepCall} from "../../../../model/step/step-call.model";
import {ArrayUtil} from "../../../../utils/array.util";
import {ComposedStepDef} from "../../../../model/step/composed-step-def.model";


export class StepCallWarningUtil {

    public static copyWarningState(destinationStepCalls: StepCall[], sourceStepCalls: StepCall[]) {
        for (let i = 0; i < destinationStepCalls.length; i++) {
            const destinationStepCall = destinationStepCalls[i];
            const sourceStepCall = sourceStepCalls[i];

            destinationStepCall.descendantsHaveWarnings = sourceStepCall.descendantsHaveWarnings;
            destinationStepCall.setWarnings(sourceStepCall.getWarnings());

            ArrayUtil.replaceElementsInArray(destinationStepCall.stepDef.warnings, sourceStepCall.stepDef.warnings);

            if (destinationStepCall.stepDef instanceof ComposedStepDef) {
                StepCallWarningUtil.copyWarningState(destinationStepCall.stepDef.stepCalls, (sourceStepCall.stepDef as ComposedStepDef).stepCalls)
            }
        }
    }
}
