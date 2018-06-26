import {Path} from "../infrastructure/path/path.model";
import {ServerTreeNode} from "./server-tree-node.model";


export interface ServerContainerTreeNode extends Serializable<any>, ServerTreeNode {
    name: string
    path: Path
    children: Array<ServerTreeNode>
}
