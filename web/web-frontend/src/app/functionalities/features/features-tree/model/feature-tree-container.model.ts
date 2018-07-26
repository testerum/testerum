
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {TestTreeNodeModel} from "./test-tree-node.model";
import {Comparable} from "../../../../model/infrastructure/comparable.model";
import {JsonTreeContainerOptions} from "../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export class FeatureTreeContainerModel extends TestTreeNodeModel implements JsonTreeContainer, Comparable<TestTreeNodeModel> {

    children: Array<TestTreeNodeModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    editable:boolean = false;

    hasOwnOrDescendantWarnings: boolean = false;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: FeatureTreeContainerModel, name: string, path: Path = null, hasOwnOrDescendantWarnings: boolean = false) {
        super(parentContainer, name, path);

        this.jsonTreeNodeState.showChildren = true;
        this.hasOwnOrDescendantWarnings = hasOwnOrDescendantWarnings;
    }

    getChildren(): Array<TestTreeNodeModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: TestTreeNodeModel, right: TestTreeNodeModel) => {
            if(!left.isContainer() && right.isContainer()) {
                return 1;
            }

            return left.compareTo(right);
        });
    }

    compareTo(other: TestTreeNodeModel): number {
        if(!other.isContainer()) {
            return -1;
        }

        if(this.name.toUpperCase() < other.name.toUpperCase()) return -1;
        if(this.name.toUpperCase() > other.name.toUpperCase()) return 1;

        return 0;
    }

    isContainer(): boolean {
        return true;
    }

    getChildContainerByName(directory: string): FeatureTreeContainerModel {
        for (let child of this.children) {
            if (child.isContainer() && child.name.toLowerCase() === directory.toLowerCase()) {
                return child as FeatureTreeContainerModel;
            }
        }

        return null;
    }

    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }
}
