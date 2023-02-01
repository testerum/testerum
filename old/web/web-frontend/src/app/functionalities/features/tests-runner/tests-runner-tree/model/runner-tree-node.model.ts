import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {RunnerTreeFilterModel} from "./filter/runner-tree-filter.model";

export abstract class RunnerTreeNodeModel extends JsonTreeNodeAbstract {

    id: string;
    path: Path;
    state: ExecutionStatusEnum = ExecutionStatusEnum.WAITING;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }

    equals(other: RunnerTreeNodeModel): boolean {
        return other.id == this.id;
    }

    changeState(newState: ExecutionStatusEnum) {
        this.state = newState;
    }

    abstract calculateNodeVisibilityBasedOnFilter(filter: RunnerTreeFilterModel): void;

}
