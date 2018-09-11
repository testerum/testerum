import {ManualUiTreeContainerStatusModel} from "./manual-ui-tree-container-status.model";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../plans/model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ManualUiTreeBaseStatusModel} from "./manual-ui-tree-base-status.model";

export class ManualUiTreeRootStatusModel extends ManualUiTreeContainerStatusModel {

    path: Path;
    name: string;
    status: ManualTestStatus;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    children: Array<ManualUiTreeBaseStatusModel> = [];
}
