import {Path} from "../../infrastructure/path/path.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class CheckComposedStepDefUsageResponse implements Serializable<CheckComposedStepDefUsageResponse>{

    isUsed: boolean;
    isUniqueStepPattern: boolean;
    pathsForAffectedTests: Array<Path> = [];
    pathsForDirectParentSteps: Array<Path> = [];
    pathsForTransitiveParentSteps: Array<Path> = [];

    deserialize(input: Object): CheckComposedStepDefUsageResponse {
        this.isUsed = input["isUsed"];

        for (let pathsForParentTest of (input['pathsForParentTests']) || []) {
            this.pathsForAffectedTests.push(Path.deserialize(pathsForParentTest));
        }

        for (let pathsForDirectParentStep of (input['pathsForDirectParentSteps']) || []) {
            this.pathsForDirectParentSteps.push(Path.deserialize(pathsForDirectParentStep));
        }

        for (let pathsForTransitiveParentStep of (input['pathsForTransitiveParentSteps']) || []) {
            this.pathsForTransitiveParentSteps.push(Path.deserialize(pathsForTransitiveParentStep));
        }

        return this;
    }

    serialize(): string {
        throw new Error("Not implemented method");
    }
}
