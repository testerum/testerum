
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeTypeEnum} from "./enums/runner-tree-node-type.enum";
import {StepCall} from "../../../../../model/step-call.model";
import {EventKey} from "../../../../../model/test/event/fields/event-key.model";
import {PositionInParent} from "../../../../../model/test/event/fields/position-in-parent.model";
import {ArrayUtil} from "../../../../../utils/array.util";
import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {StepCallContainerModel} from "../../../../../generic/components/step-call-tree/model/step-call-container.model";
import {RunnerTreeContainerNodeModel} from "./runner-tree-container-node.model";
import {RunnerTreeNodeModel} from "./runner-tree-node.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {RunnerTreeFilterModel} from "./filter/runner-tree-filter.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
export class RunnerTestTreeNodeModel extends RunnerTreeContainerNodeModel {

    id:string;
    path: Path;
    eventKey: EventKey;
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
            filter.showFailed == filter.showError &&
            filter.showError == filter.showDisabled &&
            filter.showDisabled == filter.showUndefined &&
            filter.showUndefined == filter.showSkipped) {

            this.hidden = false;
            return;
        }

        switch (this.state) {
            case ExecutionStatusEnum.WAITING: {this.hidden = !filter.showWaiting; break;}
            case ExecutionStatusEnum.PASSED: {this.hidden = !filter.showPassed; break;}
            case ExecutionStatusEnum.FAILED: {this.hidden = !filter.showFailed; break;}
            case ExecutionStatusEnum.ERROR: {this.hidden = !filter.showError; break;}
            case ExecutionStatusEnum.DISABLED: {this.hidden = !filter.showDisabled; break;}
            case ExecutionStatusEnum.UNDEFINED: {this.hidden = !filter.showUndefined; break;}
            case ExecutionStatusEnum.SKIPPED: {this.hidden = !filter.showSkipped; break;}
            default: this.hidden = false;
        }

        this.updateParentVisibility(this);
    }

    updateParentVisibility(model: RunnerTreeNodeModel) {
        if(model.getParent() instanceof JsonTreeModel) return;
        let parent = model.getParent() as RunnerTreeContainerNodeModel;
        if(parent == null) return;

        let areAllChildrenHidden = parent.areAllChildrenHidden();
        if(parent.hidden != areAllChildrenHidden) {
            parent.hidden = areAllChildrenHidden;
            this.updateParentVisibility(parent);
        }
    }
}
