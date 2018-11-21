import {ManualSelectTestsTreeNodeModel} from "./manual-select-tests-tree-node.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {Comparable} from "../../../../../../model/infrastructure/comparable.model";
import {JsonTreeNodeState} from "../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {SelectionStateEnum} from "./enum/selection-state.enum";
import {JsonTreeContainerOptions} from "../../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {JsonTreePathContainer} from "../../../../../../generic/components/json-tree/model/path/json-tree-path-container.model";

export class ManualSelectTestsTreeContainerModel extends ManualSelectTestsTreeNodeModel implements JsonTreeContainer, Comparable<ManualSelectTestsTreeNodeModel> {

    children: Array<ManualSelectTestsTreeNodeModel> = [];
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

    getChildren(): Array<ManualSelectTestsTreeNodeModel> {
        return this.children;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    sort() {
        this.children.sort((left: ManualSelectTestsTreeNodeModel, right: ManualSelectTestsTreeNodeModel) => {
            if(!left.isContainer() && right.isContainer()) {
                return 1;
            }

            return left.compareTo(right);
        });
    }

    compareTo(other: ManualSelectTestsTreeNodeModel): number {
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
                let childAsContainer = child as ManualSelectTestsTreeContainerModel;
                if(!childAsContainer.isSelectedNode()) {
                    allChildrenAreChecked = false
                }
                if (childAsContainer.isSelectedNode() || childAsContainer.isPartialSelectedNode()) {
                    newState = SelectionStateEnum.PARTIAL_SELECTED
                }
            } else {
                let childAsNode = child as ManualSelectTestsTreeNodeModel;
                if (!childAsNode.isSelected()) {
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

        if (this.parentContainer instanceof ManualSelectTestsTreeContainerModel) {
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

    getChildNodeWithPath(path: Path): ManualSelectTestsTreeNodeModel {
        for (const child of this.children) {
            // ignoring file extension to be able to match a path from "features" (having the extension ".test)
            // with a path from manual tests (having the extension ".manual_test")
            if (child.path.withoutFileExtension().equals(path.withoutFileExtension())) {
                return child;
            }
        }
        return null;
    }

    getOptions(): JsonTreeContainerOptions {
        return this.options;
    }

}
