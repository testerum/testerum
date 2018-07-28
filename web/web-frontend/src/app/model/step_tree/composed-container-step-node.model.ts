import {Path} from "../infrastructure/path/path.model";
import {ComposedStepNode} from "./composed-step-node.model";
import {ComposedStepStepNode} from "./composed-step-step-node.model";

export class ComposedContainerStepNode implements ComposedStepNode, Serializable<ComposedContainerStepNode> {

    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;
    name: string;
    children: Array<ComposedStepNode> = [];

    deserialize(input: Object): ComposedContainerStepNode {
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input['hasOwnOrDescendantWarnings'];
        this.name = input["name"];

        this.children = [];
        for (const child of input["children"]) {
            let childType = child["@type"];

            if (childType == "STEP_COMPOSED_CONTAINER") {
                this.children.push(
                    new ComposedContainerStepNode().deserialize(child)
                );
            }
            if (childType == "STEP_COMPOSED_STEP") {
                this.children.push(
                    new ComposedStepStepNode().deserialize(child)
                );
            }
        }

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }

}
