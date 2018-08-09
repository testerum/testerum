import { Path } from "../../infrastructure/path/path.model";
import { FeatureNode } from "./feature-node.model";
import { ContainerFeatureNode } from "./container-feature-node.model";
import { FeatureTreeDeserializationUtil } from "./util/feature-tree-deserialization.util";

export class FeatureFeatureNode implements Serializable<FeatureFeatureNode>, ContainerFeatureNode {
    name: string;
    path: Path;
    children: Array<FeatureNode> = [];
    hasOwnOrDescendantWarnings: boolean = false;

    deserialize(input: Object): FeatureFeatureNode {
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
