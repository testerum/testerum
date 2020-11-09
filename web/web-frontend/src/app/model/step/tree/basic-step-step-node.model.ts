import {Path} from "../../infrastructure/path/path.model";
import {BasicStepNode} from "./basic-step-node.model";
import {BasicStepDef} from "../basic-step-def.model";
import {Serializable} from "../../infrastructure/serializable.model";

export class BasicStepStepNode implements BasicStepNode, Serializable<BasicStepStepNode> {

    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;
    stepDef: BasicStepDef;

    deserialize(input: Object): BasicStepStepNode {
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input['hasOwnOrDescendantWarnings'];
        this.stepDef = new BasicStepDef().deserialize(input["stepDef"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }

}
