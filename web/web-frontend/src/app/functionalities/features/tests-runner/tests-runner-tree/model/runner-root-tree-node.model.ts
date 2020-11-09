import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {EventKey} from "../../../../../model/test/event/fields/event-key.model";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunnerTestTreeNodeModel} from "./runner-test-tree-node.model";
import {RunnerTreeContainerNodeModel} from "./runner-tree-container-node.model";
import {RunnerTreeNodeModel} from "./runner-tree-node.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {RunnerTreeFilterModel} from "./filter/runner-tree-filter.model";

export class RunnerRootTreeNodeModel extends RunnerTreeContainerNodeModel {

    id:string;
    path: Path;
    eventKey: EventKey;
    state:ExecutionStatusEnum = ExecutionStatusEnum.WAITING;
    text:string = "Test Suite";

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    children: Array<RunnerTestTreeNodeModel> = [];

    getChildren(): Array<RunnerTestTreeNodeModel> {
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
