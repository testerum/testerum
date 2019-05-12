import {RunnerConfigTestTreeBaseModel} from "./runner-config-test-tree-base.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../../../../manual/plans/model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";

export class RunnerConfigTestTreeContainerModel extends RunnerConfigTestTreeBaseModel implements JsonTreeContainer {

    path: Path;
    name: string;
    status: ManualTestStatus;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    selected: boolean = false;
    children: Array<RunnerConfigTestTreeBaseModel> = [];

    private _containerOptions: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: ManualTestStatus) {
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
}
