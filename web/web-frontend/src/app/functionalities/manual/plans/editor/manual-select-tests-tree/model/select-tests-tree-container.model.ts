import {SelectTestsTreeNodeModel} from "./select-tests-tree-node.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {Comparable} from "../../../../../../model/infrastructure/comparable.model";
import {JsonTreeNodeState} from "../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {SelectionStateEnum} from "./enum/selection-state.enum";
import {JsonTreeContainerOptions} from "../../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {JsonTreePathContainer} from "../../../../../../generic/components/json-tree/model/path/json-tree-path-container.model";

export class SelectTestsTreeContainerModel extends SelectTestsTreeNodeModel implements JsonTreeContainer, Comparable<SelectTestsTreeNodeModel> {

    children: Array<SelectTestsTreeNodeModel> = [];
    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();

    isRootPackage:boolean = false;
    editable:boolean = false;

    selectedState: SelectionStateEnum = SelectionStateEnum.NOT_SELECTED;

    private options: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreePathContainer, name: string, path: Path = null, selectedState: SelectionStateEnum = SelectionStateEnum.NOT_SELECTED) {
        super(parentContainer, name, path, false);

        this.selectedState = selectedState;
        this.jsonTreeNodeState.showChildren = true;
    }

    getChildren(): Array<SelectTestsTreeNodeModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: SelectTestsTreeNodeModel, right: SelectTestsTreeNodeModel) => {
            if(!left.isContainer() && right.isContainer()) {
                return 1;
            }

            return left.compareTo(right);
        });
    }

    compareTo(other: SelectTestsTreeNodeModel): number {
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

    isSelectedNode(): boolean {
        return this.selectedState == SelectionStateEnum.SELECTED
    }

    isPartialSelectedNode(): boolean {
        return this.selectedState == SelectionStateEnum.PARTIAL_SELECTED
    }

    isNotSelectedNode(): boolean {
        return this.selectedState == SelectionStateEnum.NOT_SELECTED
    }

    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    calculateCheckState() {
        let allChildrenAreChecked: boolean = true;

        let newState = SelectionStateEnum.NOT_SELECTED;
        for (const child of this.children) {
            if (child.isContainer()) {
                let childAsContainer = child as SelectTestsTreeContainerModel;
                if(!childAsContainer.isSelectedNode()) {
                    allChildrenAreChecked = false
                }
                if (childAsContainer.isSelectedNode() || childAsContainer.isPartialSelectedNode()) {
                    newState = SelectionStateEnum.PARTIAL_SELECTED
                }
            } else {
                let childAsNode = child as SelectTestsTreeNodeModel;
                if (!childAsNode.isSelected) {
                    allChildrenAreChecked = false;
                } else {
                    newState = SelectionStateEnum.PARTIAL_SELECTED
                }
            }
        }

        if (allChildrenAreChecked) {
            newState = SelectionStateEnum.SELECTED;
        }
        this.selectedState = newState;

        if (this.parentContainer instanceof SelectTestsTreeContainerModel) {
            this.parentContainer.calculateCheckState();
        }
    }

    containsChildNodeWithPath(path: Path): boolean {
        for (const child of this.children) {
            if (child.path.equals(path)) {
                return true;
            }
        }
        return false;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }
}
