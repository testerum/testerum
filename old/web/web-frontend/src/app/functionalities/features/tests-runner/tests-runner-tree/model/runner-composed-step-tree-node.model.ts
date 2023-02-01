import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {StepCall} from "../../../../../model/step/step-call.model";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {RunnerTreeNodeModel} from "./runner-tree-node.model";
import {RunnerTreeContainerNodeModel} from "./runner-tree-container-node.model";
import {RunnerTreeFilterModel} from "./filter/runner-tree-filter.model";

export class RunnerComposedStepTreeNodeModel extends RunnerTreeContainerNodeModel {

    id:string;
    path: Path;
    state:ExecutionStatusEnum = ExecutionStatusEnum.WAITING;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    children: Array<RunnerTreeNodeModel> = [];

    stepCall:StepCall;

    getChildren(): Array<RunnerTreeNodeModel> {
        return this.children;
    }

    equals(other: RunnerTreeNodeModel): boolean {
        return super.equals(other);
    }

    changeState(newState:ExecutionStatusEnum) {
        super.changeState(newState);
    }

    calculateNodeVisibilityBasedOnFilter(filter: RunnerTreeFilterModel): void {
        // only TestNode has an implementation
    }
}
