import {Path} from "../infrastructure/path/path.model";
import {StepPattern} from "../text/step-pattern.model";
import {ComposedStepNode} from "./composed-step-node.model";
import {StepPhaseEnum} from "../enums/step-phase.enum";

export class ComposedStepStepNode implements ComposedStepNode, Serializable<ComposedStepStepNode> {

    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;
    phase: StepPhaseEnum;
    stepPattern:StepPattern = new StepPattern();

    deserialize(input: Object): ComposedStepStepNode {
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input['hasOwnOrDescendantWarnings'];
        this.phase = StepPhaseEnum[""+input["phase"]];
        this.stepPattern = new StepPattern().deserialize(input["stepPattern"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }

}
