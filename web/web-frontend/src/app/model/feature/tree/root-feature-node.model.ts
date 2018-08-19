import { Path } from "../../infrastructure/path/path.model";
import { FeatureNode } from "./feature-node.model";
import { ContainerFeatureNode } from "./container-feature-node.model";
import { FeatureTreeDeserializationUtil } from "./util/feature-tree-deserialization.util";
import {Serializable} from "../../infrastructure/serializable.model";

export class RootFeatureNode implements Serializable<RootFeatureNode>, ContainerFeatureNode {
    name: string;
    path: Path;
    children: Array<FeatureNode> = [];
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): RootFeatureNode {
        this.name = input["name"];
        this.path = Path.deserialize(input["path"]);
        this.hasOwnOrDescendantWarnings = input["hasOwnOrDescendantWarnings"];
        this.children = FeatureTreeDeserializationUtil.deserialize(input["children"] || []);

        return this;
    }

    serialize(): string {
        throw Error("method not implemented")
    }
}
