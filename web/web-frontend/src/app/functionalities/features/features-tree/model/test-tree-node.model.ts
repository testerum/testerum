
import {JsonTreePathNode} from "../../../../generic/components/json-tree/model/path/json-tree-path-node.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {StepPattern} from "../../../../model/text/step-pattern.model";
import {StepDef} from "../../../../model/step-def.model";
import {TestModel} from "../../../../model/test/test.model";
import {Comparable} from "../../../../model/infrastructure/comparable.model";
import {FeatureTreeContainerModel} from "./feature-tree-container.model";
import {TestProperties} from "../../../../model/test/test-properties.model";

export class TestTreeNodeModel extends JsonTreePathNode implements Comparable<TestTreeNodeModel> {

    name: string;
    path: Path;

    testProperties: TestProperties
    hasOwnOrDescendantWarnings: boolean = false;

    constructor(parentContainer: FeatureTreeContainerModel, name: string, path?: Path, testProperties: TestProperties = new TestProperties(), hasOwnOrDescendantWarnings: boolean = false) {
        super(parentContainer, path);
        this.name = name;
        this.hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings;
        this.testProperties = testProperties;
    }

    compareTo(other: TestTreeNodeModel): number {

        let thisName = this.name;
        let otherName = other.name;

        if(thisName.toUpperCase() < otherName.toUpperCase()) return -1;
        if(thisName.toUpperCase() > otherName.toUpperCase()) return 1;

        return 0;
    }
}
