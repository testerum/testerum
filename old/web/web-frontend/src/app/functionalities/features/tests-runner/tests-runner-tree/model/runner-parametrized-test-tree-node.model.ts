import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunnerTreeContainerNodeModel} from "./runner-tree-container-node.model";
import {RunnerTreeNodeModel} from "./runner-tree-node.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {RunnerTreeFilterModel} from "./filter/runner-tree-filter.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {RunnerRootTreeNodeModel} from "./runner-root-tree-node.model";

export class RunnerParametrizedTestTreeNodeModel extends RunnerTreeContainerNodeModel {

    id:string;
    path: Path;
    state:ExecutionStatusEnum = ExecutionStatusEnum.WAITING;
    text:string;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    children: Array<RunnerTreeNodeModel> = [];

    getChildren(): Array<RunnerTreeNodeModel> {
        return this.children;
    }

    equals(other: RunnerTreeNodeModel): boolean {
        return super.equals(other);
    }

    changeState(newState:ExecutionStatusEnum) {
        super.changeState(newState);
    }

    calculateNodeVisibilityBasedOnFilter(filter: RunnerTreeFilterModel) {

        if (filter.showWaiting == filter.showPassed &&
            filter.showPassed == filter.showFailed &&
            filter.showFailed == filter.showDisabled &&
            filter.showDisabled == filter.showUndefined &&
            filter.showUndefined == filter.showSkipped) {

            this.hidden = false;
            this.updateParentVisibility(this);
            return;
        }

        switch (this.state) {
            case ExecutionStatusEnum.WAITING: {this.hidden = !filter.showWaiting; break;}
            case ExecutionStatusEnum.PASSED: {this.hidden = !filter.showPassed; break;}
            case ExecutionStatusEnum.FAILED: {this.hidden = !filter.showFailed; break;}
            case ExecutionStatusEnum.DISABLED: {this.hidden = !filter.showDisabled; break;}
            case ExecutionStatusEnum.UNDEFINED: {this.hidden = !filter.showUndefined; break;}
            case ExecutionStatusEnum.SKIPPED: {this.hidden = !filter.showSkipped; break;}
            default: this.hidden = false;
        }

        this.updateParentVisibility(this);
    }

    updateParentVisibility(model: RunnerTreeNodeModel) {
        if(model.getParent() instanceof JsonTreeModel) return;
        if(model.getParent() instanceof RunnerRootTreeNodeModel) return;
        let parent = model.getParent() as RunnerTreeContainerNodeModel;
        if(parent == null) return;

        let areAllChildrenHidden = parent.areAllChildrenHidden();
        if(parent.hidden != areAllChildrenHidden) {
            parent.hidden = areAllChildrenHidden;
            this.updateParentVisibility(parent);
        }
    }
}
