import { Path } from "../../infrastructure/path/path.model";
import {Serializable} from "../../infrastructure/serializable.model";

export interface RunnerNode extends Serializable<any> {
    id: string;
    path: Path
}
