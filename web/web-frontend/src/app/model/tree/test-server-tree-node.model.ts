import {Path} from "../infrastructure/path/path.model";
import {ServerTreeNode} from "./server-tree-node.model";
import {TestProperties} from "../test/test-properties.model";

export class TestServerTreeNode implements Serializable<TestServerTreeNode>, ServerTreeNode {
    name: string;
    properties: TestProperties;
    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): TestServerTreeNode {
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);
        this.properties = new TestProperties().deserialize(input['properties']);
        this.hasOwnOrDescendantWarnings = input["hasOwnOrDescendantWarnings"];

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
