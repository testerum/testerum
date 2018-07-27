import {Path} from "../infrastructure/path/path.model";


export interface ServerMainNode extends Serializable<any> {
    name: string
    path: Path
}
