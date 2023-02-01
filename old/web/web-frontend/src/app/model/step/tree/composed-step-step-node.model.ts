import {Path} from "../../infrastructure/path/path.model";
import {ComposedStepNode} from "./composed-step-node.model";
import {ComposedStepDef} from "../composed-step-def.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class ComposedStepStepNode implements ComposedStepNode, Serializable<ComposedStepStepNode> {

    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;
    stepDef: ComposedStepDef;
    isUsedStep: boolean = false;

    deserialize(input: Object): ComposedStepStepNode {
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input['hasOwnOrDescendantWarnings'];
        this.stepDef = new ComposedStepDef().deserialize(input["stepDef"]);
        this.isUsedStep = input['isUsedStep'];

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
