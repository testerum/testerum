import { Path } from "../../infrastructure/path/path.model";
import {Serializable} from "../../infrastructure/serializable.model";

export interface FeatureNode extends Serializable<any> {
    name: string
    path: Path
}
