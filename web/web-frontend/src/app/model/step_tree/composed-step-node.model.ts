import {Path} from "../infrastructure/path/path.model";

export interface ComposedStepNode {
    path: Path
    hasOwnOrDescendantWarnings: boolean
}
