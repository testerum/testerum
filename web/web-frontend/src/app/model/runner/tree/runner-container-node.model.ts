import {RunnerNode} from "./runner-node.model";
import {Path} from "../../infrastructure/path/path.model";

export interface RunnerContainerNode extends Serializable<any>, RunnerNode {
    id: string
    name: string
    path: Path
    children: Array<RunnerNode>
}
