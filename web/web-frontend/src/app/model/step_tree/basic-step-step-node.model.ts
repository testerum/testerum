import {Path} from "../infrastructure/path/path.model";
import {StepPattern} from "../text/step-pattern.model";
import {BasicStepNode} from "./basic-step-node.model";
import {StepPhaseEnum} from "../enums/step-phase.enum";

export class BasicStepStepNode implements BasicStepNode, Serializable<BasicStepStepNode> {

    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;
    phase: StepPhaseEnum;
    stepPattern:StepPattern = new StepPattern();


    deserialize(input: Object): BasicStepStepNode {
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
