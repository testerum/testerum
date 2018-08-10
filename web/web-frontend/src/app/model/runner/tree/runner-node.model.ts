import { Path } from "../../infrastructure/path/path.model";

export interface RunnerNode extends Serializable<any> {
    id: string;
    path: Path
    name: string
}
