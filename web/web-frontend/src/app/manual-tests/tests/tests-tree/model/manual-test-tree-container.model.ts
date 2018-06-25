
import {JsonTreeContainer} from "../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeState} from "../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ManualTestTreeNodeModel} from "./manual-test-tree-node.model";
import {Comparable} from "../../../../model/infrastructure/comparable.model";
import {JsonTreeContainerOptions} from "../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export class ManualTestTreeContainerModel extends ManualTestTreeNodeModel implements JsonTreeContainer, Comparable<ManualTestTreeNodeModel> {

    children: Array<ManualTestTreeNodeModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    editable:boolean = false;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: ManualTestTreeContainerModel, name: string, path: Path = null) {
        super(null, name, path, parentContainer);

        this.jsonTreeNodeState.showChildren = true;
    }

    getChildren(): Array<ManualTestTreeNodeModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: ManualTestTreeNodeModel, right: ManualTestTreeNodeModel) => {
            if(!left.isContainer() && right.isContainer()) {
                return 1;
            }

            return left.compareTo(right);
        });
    }

    compareTo(other: ManualTestTreeNodeModel): number {
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

    getChildContainerByName(directory: string): ManualTestTreeContainerModel {
        for (let child of this.children) {
            if (child.isContainer() && child.name.toLowerCase() === directory.toLowerCase()) {
                return child as ManualTestTreeContainerModel;
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
