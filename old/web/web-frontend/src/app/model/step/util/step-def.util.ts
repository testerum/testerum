import {StepCall} from "../step-call.model";
import {ComposedStepDef} from "../composed-step-def.model";

export class StepDefUtil {

    static fixStepDefInstance(stepCalls: Array<StepCall>) {
        let uniqueStepDefs = new Map();
        for (const stepCall of stepCalls) {
            if (!(stepCall.stepDef instanceof ComposedStepDef)) {
                continue
            }

            let stepDefKey = stepCall.stepDef.path.toString();
            let sharedStepDefInstance = uniqueStepDefs.get(stepDefKey);

            if (sharedStepDefInstance) {
                stepCall.stepDef = sharedStepDefInstance;
            } else {
                uniqueStepDefs.set(stepDefKey, stepCall.stepDef)
            }
        }
    }
}
