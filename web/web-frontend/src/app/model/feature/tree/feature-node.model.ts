import { Path } from "../../infrastructure/path/path.model";


export interface FeatureNode extends Serializable<any> {
    name: string
    path: Path
}
