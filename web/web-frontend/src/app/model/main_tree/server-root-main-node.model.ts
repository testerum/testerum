import {Path} from "../infrastructure/path/path.model";
import {ServerMainNode} from "./server-main-node.model";
import {ServerContainerMainNode} from "./server-container-main-node.model";
import {ServerTestMainNode} from "./server-test-main-node.model";
import {ServerFeatureMainNode} from "./server-feature-main-node.model";

export class ServerRootMainNode implements Serializable<ServerRootMainNode>, ServerContainerMainNode {
    name: string;
    path: Path;
    children: Array<ServerMainNode> = [];
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): ServerRootMainNode {
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input["hasOwnOrDescendantWarnings"];

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
