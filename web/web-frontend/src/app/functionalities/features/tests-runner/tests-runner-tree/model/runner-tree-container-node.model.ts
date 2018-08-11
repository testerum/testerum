
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
import {RunnerTreeNodeModel} from "./runner-tree-node.model";
import {JsonTreeContainerOptions} from "../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {JsonTreeNode} from "../../../../../generic/components/json-tree/model/json-tree-node.model";
export abstract class RunnerTreeContainerNodeModel extends RunnerTreeNodeModel implements JsonTreeContainer{

    id: string;
    path: Path;
    eventKey: EventKey;
    state:ExecutionStatusEnum = ExecutionStatusEnum.WAITING;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;

    private _containerOptions: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer) {
        super(parentContainer);
    }

    abstract getChildren(): Array<RunnerTreeNodeModel>;

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

    equals(other: RunnerTreeNodeModel): boolean {
        return super.equals(other);
    }

    changeState(newState:ExecutionStatusEnum) {
        super.changeState(newState);
    }
}
