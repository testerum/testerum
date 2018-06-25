
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {StepPattern} from "../../../../model/text/step-pattern.model";
import {StepDef} from "../../../../model/step-def.model";
import {TestModel} from "../../../../model/test/test.model";
import {Comparable} from "../../../../model/infrastructure/comparable.model";
import {FeatureTreeContainerModel} from "./feature-tree-container.model";

export class TestTreeNodeModel extends JsonTreePathNode implements Comparable<TestTreeNodeModel> {

    name: string;
    path: Path;
    id: string; //TODO: to be removed when we have resources as file

    constructor(parentContainer: FeatureTreeContainerModel, id: string, name: string, path?: Path) {
        super(parentContainer, path);
        this.id = id;
        this.name = name;
    }

    compareTo(other: TestTreeNodeModel): number {

        let thisName = this.name;
        let otherName = other.name;

        if(thisName.toUpperCase() < otherName.toUpperCase()) return -1;
        if(thisName.toUpperCase() > otherName.toUpperCase()) return 1;

        return 0;
    }
}
