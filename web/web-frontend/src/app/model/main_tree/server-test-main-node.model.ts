import {Path} from "../infrastructure/path/path.model";
import {ServerMainNode} from "./server-main-node.model";
import {TestProperties} from "../test/test-properties.model";

export class ServerTestMainNode implements Serializable<ServerTestMainNode>, ServerMainNode {
    name: string;
    properties: TestProperties;
    path: Path;
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): ServerTestMainNode {
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
