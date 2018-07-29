import {Path} from "../../infrastructure/path/path.model";
import {BasicStepNode} from "./basic-step-node.model";
import {BasicStepStepNode} from "./basic-step-step-node.model";

export class BasicContainerStepNode implements BasicStepNode, Serializable<BasicContainerStepNode> {

    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;
    name: string;
    children: Array<BasicStepNode> = [];

    deserialize(input: Object): BasicContainerStepNode {
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input['hasOwnOrDescendantWarnings'];
        this.name = input["name"];

        this.children = [];
        for (const child of input["children"]) {
            let childType = child["@type"];

            if (childType == "STEP_BASIC_CONTAINER") {
                this.children.push(
                    new BasicContainerStepNode().deserialize(child)
                );
            }
            if (childType == "STEP_BASIC_STEP") {
                this.children.push(
                    new BasicStepStepNode().deserialize(child)
                );
            }
        }

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }

}
