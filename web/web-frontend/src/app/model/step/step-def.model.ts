import {StepPhaseEnum} from "../enums/step-phase.enum";
import {TreeNodeModel} from "../infrastructure/tree-node.model";
import {StepPattern} from "../text/step-pattern.model";
import {Path} from "../infrastructure/path/path.model";
import {Warning} from "../warning/Warning";
import {Serializable} from "../infrastructure/serializable.model";

export interface StepDef extends Serializable<any>, TreeNodeModel {
    id: string
    path: Path
    phase: StepPhaseEnum
    stepPattern: StepPattern
    description: string

    warnings: Array<Warning>;
    descendantsHaveWarnings: boolean;

    hasOwnOrDescendantWarnings: boolean

    clone():StepDef;
    toString(): string;
}
