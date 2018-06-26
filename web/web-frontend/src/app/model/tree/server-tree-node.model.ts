import {Path} from "../infrastructure/path/path.model";


export interface ServerTreeNode extends Serializable<any> {
    name: string
    path: Path
}
