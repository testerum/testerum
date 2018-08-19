import { Path } from "../../infrastructure/path/path.model";
import { FeatureNode } from "./feature-node.model";
import {Serializable} from "../../infrastructure/serializable.model";

export interface ContainerFeatureNode extends Serializable<any>, FeatureNode {
    name: string
    path: Path
    children: Array<FeatureNode>
}
