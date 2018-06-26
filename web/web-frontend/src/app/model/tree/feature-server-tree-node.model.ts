import {Path} from "../infrastructure/path/path.model";
import {ServerTreeNode} from "./server-tree-node.model";
import {ServerContainerTreeNode} from "./server-container-tree-node.model";
import {TestServerTreeNode} from "./test-server-tree-node.model";

export class FeatureServerTreeNode implements Serializable<FeatureServerTreeNode>, ServerContainerTreeNode {
    name: string;
    path: Path;
    children: Array<ServerTreeNode> = [];

    deserialize(input: Object): FeatureServerTreeNode {
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);

        for (const child of input["children"]) {
            if (child["@type"] == "FEATURE_NODE") {
                this.children.push(
                    new FeatureServerTreeNode().deserialize(child)
                );
            }
            if (child["@type"] == "TEST_NODE") {
                this.children.push(
                    new TestServerTreeNode().deserialize(child)
                );
            }
        }

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
