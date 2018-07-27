import {Path} from "../infrastructure/path/path.model";
import {ServerMainNode} from "./server-main-node.model";


export interface ServerContainerMainNode extends Serializable<any>, ServerMainNode {
    name: string
    path: Path
    children: Array<ServerMainNode>
}
