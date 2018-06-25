import {Path} from "../infrastructure/path/path.model";
import {StepCall} from "../step-call.model";

export class CheckComposedStepDefUpdateCompatibilityResponse implements Serializable<CheckComposedStepDefUpdateCompatibilityResponse>{

    isCompatible: boolean;
    isUniqueStepPattern: boolean;
    pathsForAffectedTests: Array<Path> = [];
    pathsForDirectAffectedSteps: Array<Path> = [];
    pathsForTransitiveAffectedSteps: Array<Path> = [];

    deserialize(input: Object): CheckComposedStepDefUpdateCompatibilityResponse {
        this.isCompatible = input["compatible"];
        this.isUniqueStepPattern = input["uniqueStepPattern"];

        for (let pathsForAffectedTest of (input['pathsForAffectedTests']) || []) {
            this.pathsForAffectedTests.push(Path.deserialize(pathsForAffectedTest));
        }

        for (let pathsForDirectAffectedStep of (input['pathsForDirectAffectedSteps']) || []) {
            this.pathsForDirectAffectedSteps.push(Path.deserialize(pathsForDirectAffectedStep));
        }

        for (let pathsForTransitiveAffectedStep of (input['pathsForTransitiveAffectedSteps']) || []) {
            this.pathsForTransitiveAffectedSteps.push(Path.deserialize(pathsForTransitiveAffectedStep));
        }

        return this;
    }

    serialize(): string {
        throw new Error("Not implemented method");
    }
}
