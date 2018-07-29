
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {StepPattern} from "../../../../model/text/step-pattern.model";
import {StepDef} from "../../../../model/step-def.model";
import {StepTreeContainerModel} from "./step-tree-container.model";
import {JsonTreePathContainer} from "../../../../generic/components/json-tree/model/path/json-tree-path-container.model";

export class StepTreeNodeModel extends JsonTreePathNode {

    readonly path: Path;
    stepDef: StepDef;
    isComposedStep: boolean;

    hasOwnOrDescendantWarnings: boolean = false;

    constructor(parentContainer: JsonTreePathContainer,
                path: Path,
                stepDef: StepDef,
                isComposedStep: boolean,
                hasOwnOrDescendantWarnings: boolean = false) {

        super(parentContainer, path);
        this.stepDef = stepDef;
        this.isComposedStep = isComposedStep;
        this.hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings;
    }

    toString(): string {
        return this.stepDef.toString();
    }
}
