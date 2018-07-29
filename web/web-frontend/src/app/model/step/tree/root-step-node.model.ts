import {BasicContainerStepNode} from "./basic-container-step-node.model";
import {ComposedContainerStepNode} from "./composed-container-step-node.model";

export class RootStepNode implements Serializable<RootStepNode> {

    name: string;
    basicStepsRoot: BasicContainerStepNode;
    composedStepsRoot: ComposedContainerStepNode;
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): RootStepNode {
        this.name = input["name"];
        this.basicStepsRoot = new BasicContainerStepNode().deserialize(input["basicStepsRoot"]);
        this.composedStepsRoot = new ComposedContainerStepNode().deserialize(input["composedStepsRoot"]);
        this.hasOwnOrDescendantWarnings = input["hasOwnOrDescendantWarnings"];

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }

}
