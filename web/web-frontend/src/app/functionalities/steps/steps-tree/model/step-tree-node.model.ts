
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {StepPattern} from "../../../../model/text/step-pattern.model";
import {StepDef} from "../../../../model/step-def.model";
import {StepTreeContainerModel} from "./step-tree-container.model";

export class StepTreeNodeModel extends JsonTreePathNode {

    name: string;
    readonly path: Path;
    stepDef: StepDef;

    constructor(parentContainer: StepTreeContainerModel, path?: Path) {
        super(parentContainer, path);
    }
}
