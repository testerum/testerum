import {Path} from "../../infrastructure/path/path.model";

export interface BasicStepNode {
    path: Path
    hasOwnOrDescendantWarnings: boolean
}
