import {RunnerConfigTestTreeBaseModel} from "./runner-config-test-tree-base.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../../../../manual/plans/model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {RunnerConfigTestTreeNodeStatusEnum} from "./enum/runner-config-test-tree-node-status.enum";
import {SelectionStateEnum} from "../../../../../../manual/plans/editor/manual-select-tests-tree/model/enum/selection-state.enum";
import {ManualSelectTestsTreeNodeModel} from "../../../../../../manual/plans/editor/manual-select-tests-tree/model/manual-select-tests-tree-node.model";
import {RunnerConfigTestTreeNodeModel} from "./runner-config-test-tree-node.model";

export class RunnerConfigTestTreeContainerModel extends RunnerConfigTestTreeBaseModel implements JsonTreeContainer {

    path: Path;
    name: string;
    status: RunnerConfigTestTreeNodeStatusEnum = RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;
    children: Array<RunnerConfigTestTreeBaseModel> = [];

    private _containerOptions: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: RunnerConfigTestTreeNodeStatusEnum) {
        super(parentContainer, path, name, status);
    }

    getChildren(): Array<RunnerConfigTestTreeBaseModel> {
        return this.children;
    }

    getParent(): JsonTreeContainer {
        return this.parentContainer;
    }

    isContainer(): boolean {
        return true;
    }

    hasChildren(): boolean {
        return this.getChildren().length != 0;
    }

    getNodeState(): JsonTreeNodeState {
        return this.jsonTreeNodeState;
    }

    getOptions(): JsonTreeContainerOptions {
        return this._containerOptions;
    }

    isHidden(): boolean {
        return this.hidden;
    }

    setHidden(isHidden: boolean) {
        this.hidden = isHidden;
    }

    areAllChildrenHidden(): boolean {
        return this.getChildren().every(it => {return it.hidden})
    }

    isSelected(): boolean {
        return this.selected;
    }

    setSelected(isSelected: boolean) {
        this.selected = isSelected;
    }

    calculateCheckState() {
        let allChildrenAreChecked: boolean = true;

        let newState = RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED;
        for (const child of this.children) {
            if (child.isContainer()) {
                let childAsContainer = child as RunnerConfigTestTreeContainerModel;
                if(!childAsContainer.isSelectedNode()) {
                    allChildrenAreChecked = false
                }
                if (childAsContainer.isSelectedNode() || childAsContainer.isPartialSelectedNode()) {
                    newState = RunnerConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED
                }
            } else {
                let childAsNode = child as RunnerConfigTestTreeNodeModel;
                if (!childAsNode.isSelected()) {
                    allChildrenAreChecked = false;
                } else {
                    newState = RunnerConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED
                }
            }
        }

        if (allChildrenAreChecked) {
            newState = RunnerConfigTestTreeNodeStatusEnum.SELECTED;
        }
        this.status = newState;

        if (this.parentContainer instanceof RunnerConfigTestTreeContainerModel) {
            this.parentContainer.calculateCheckState();
        }
    }
}
