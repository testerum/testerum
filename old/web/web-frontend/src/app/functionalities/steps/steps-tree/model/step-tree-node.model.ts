import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {StepDef} from "../../../../model/step/step-def.model";
import {JsonTreePathContainer} from "../../../../generic/components/json-tree/model/path/json-tree-path-container.model";

export class StepTreeNodeModel extends JsonTreePathNode {

    path: Path;
    stepDef: StepDef;
    isComposedStep: boolean;

    hasOwnOrDescendantWarnings: boolean = false;
    isUsedStep: boolean = false;

    constructor(parentContainer: JsonTreePathContainer,
                path: Path,
                stepDef: StepDef,
                isComposedStep: boolean,
                hasOwnOrDescendantWarnings: boolean = false,
                isUsedStep: boolean = true) {

        super(parentContainer, path);
        this.stepDef = stepDef;
        this.isComposedStep = isComposedStep;
        this.hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings;
        this.isUsedStep = isUsedStep;
    }

    toString(): string {
        return this.stepDef.toString();
    }
}
