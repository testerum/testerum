import { Path } from "../../infrastructure/path/path.model";
import { FeatureNode } from "./feature-node.model";


export interface ContainerFeatureNode extends Serializable<any>, FeatureNode {
    name: string
    path: Path
    children: Array<FeatureNode>
}
