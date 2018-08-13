
import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeTypeEnum} from "./enums/runner-tree-node-type.enum";
import {StepCall} from "../../../../../model/step-call.model";
import {EventKey} from "../../../../../model/test/event/fields/event-key.model";
import {PositionInParent} from "../../../../../model/test/event/fields/position-in-parent.model";
import {ArrayUtil} from "../../../../../utils/array.util";
import {JsonTreeContainerAbstract} from "../../../../../generic/components/json-tree/model/json-tree-container.abstract";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {RunnerTreeFilterModel} from "./filter/runner-tree-filter.model";
import {RunnerTestTreeNodeModel} from "./runner-test-tree-node.model";
import {RunnerTreeContainerNodeModel} from "./runner-tree-container-node.model";
export abstract class RunnerTreeNodeModel extends JsonTreeNodeAbstract {

    id: string;
    path: Path;
    eventKey: EventKey;
    state:ExecutionStatusEnum = ExecutionStatusEnum.WAITING;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }

    equals(other: RunnerTreeNodeModel): boolean {
        return other.id == this.id;
    }

    changeState(newState:ExecutionStatusEnum) {
        this.state = newState;
    }

    abstract calculateNodeVisibilityBasedOnFilter(filter: RunnerTreeFilterModel): void;

}
