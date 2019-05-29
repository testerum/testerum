import {RunConfigTestTreeBaseModel} from "./run-config-test-tree-base.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {RunConfigTestTreeNodeStatusEnum} from "./enum/run-config-test-tree-node-status.enum";
import {RunConfigTestTreeNodeModel} from "./run-config-test-tree-node.model";

export class RunConfigTestTreeContainerModel extends RunConfigTestTreeBaseModel implements JsonTreeContainer {

    path: Path;
    name: string;
    status: RunConfigTestTreeNodeStatusEnum = RunConfigTestTreeNodeStatusEnum.NOT_SELECTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;
    children: Array<RunConfigTestTreeBaseModel> = [];

    private _containerOptions: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: RunConfigTestTreeNodeStatusEnum) {
        super(parentContainer, path, name, status);
    }

    getChildren(): Array<RunConfigTestTreeBaseModel> {
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

    calculateCheckState() {
        let allChildrenAreChecked: boolean = true;

        let newState = RunConfigTestTreeNodeStatusEnum.NOT_SELECTED;
        for (const child of this.children) {
            if (child.isContainer()) {
                let childAsContainer = child as RunConfigTestTreeContainerModel;
                if(!childAsContainer.isSelectedNode()) {
                    allChildrenAreChecked = false
                }
                if (childAsContainer.isSelectedNode() || childAsContainer.isPartialSelectedNode()) {
                    newState = RunConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED
                }
            } else {
                let childAsNode = child as RunConfigTestTreeNodeModel;
                if (!childAsNode.isSelected()) {
                    allChildrenAreChecked = false;
                } else {
                    newState = RunConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED
                }
            }
        }

        if (allChildrenAreChecked) {
            newState = RunConfigTestTreeNodeStatusEnum.SELECTED;
        }
        this.status = newState;

        if (this.parentContainer && this.parentContainer instanceof RunConfigTestTreeContainerModel) {
            this.parentContainer.calculateCheckState();
        }
    }
}
