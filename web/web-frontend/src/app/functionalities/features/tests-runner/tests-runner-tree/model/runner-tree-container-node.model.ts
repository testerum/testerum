import {ExecutionStatusEnum} from "../../../../../model/test/event/enums/execution-status.enum";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunnerTreeNodeModel} from "./runner-tree-node.model";
import {JsonTreeContainerOptions} from "../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export abstract class RunnerTreeContainerNodeModel extends RunnerTreeNodeModel implements JsonTreeContainer{

    id: string;
    path: Path;
    state:ExecutionStatusEnum = ExecutionStatusEnum.WAITING;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;
    enabled: boolean = true;

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

    isSelected(): boolean {
        return this.selected;
    }

    setSelected(isSelected: boolean) {
        this.selected = isSelected;
    }

    equals(other: RunnerTreeNodeModel): boolean {
        return super.equals(other);
    }

    changeState(newState:ExecutionStatusEnum) {
        super.changeState(newState);
    }

    areAllChildrenHidden(): boolean {
        return this.getChildren().every(it => {return it.hidden})
    }
}
