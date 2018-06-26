import {Path} from "../infrastructure/path/path.model";
import {ServerTreeNode} from "./server-tree-node.model";

export class TestServerTreeNode implements Serializable<TestServerTreeNode>, ServerTreeNode {
    name: string;
    path: Path;

    deserialize(input: Object): TestServerTreeNode {
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
