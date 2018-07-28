import {Path} from "../infrastructure/path/path.model";
import {ServerMainNode} from "./server-main-node.model";
import {ServerContainerMainNode} from "./server-container-main-node.model";
import {ServerTestMainNode} from "./server-test-main-node.model";

export class ServerFeatureMainNode implements Serializable<ServerFeatureMainNode>, ServerContainerMainNode {
    name: string;
    path: Path;
    children: Array<ServerMainNode> = [];
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): ServerFeatureMainNode {
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input["hasOwnOrDescendantWarnings"];

        this.children = [];
        for (const child of input["children"]) {
            if (child["@type"] == "MAIN_FEATURE") {
                this.children.push(
                    new ServerFeatureMainNode().deserialize(child)
                );
            }
            if (child["@type"] == "MAIN_TEST") {
                this.children.push(
                    new ServerTestMainNode().deserialize(child)
                );
            }
        }

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
