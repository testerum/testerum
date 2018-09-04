import {ManualTestStatus} from "../../../model/enums/manual-test-status.enum";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ManualUiTreeBaseStatusModel} from "./manual-ui-tree-base-status.model";
import {JsonTreeNodeState} from "../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainerOptions} from "../../../../../../generic/components/json-tree/model/behavior/JsonTreeContainerOptions";
import {RunnerTreeFilterModel} from "../../../../../features/tests-runner/tests-runner-tree/model/filter/runner-tree-filter.model";
import {JsonTreeModel} from "../../../../../../generic/components/json-tree/model/json-tree.model";

export class ManualUiTreeContainerStatusModel extends ManualUiTreeBaseStatusModel implements JsonTreeContainer {

    path: Path;
    name: string;
    status: ManualTestStatus = ManualTestStatus.NOT_EXECUTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    children: Array<ManualUiTreeBaseStatusModel> = [];

    private _containerOptions: JsonTreeContainerOptions = new JsonTreeContainerOptions();

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: ManualTestStatus) {
        super(parentContainer, path, name, status);
    }

    getChildren(): Array<ManualUiTreeBaseStatusModel> {
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

}
