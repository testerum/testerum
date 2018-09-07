import {ManualUiTreeBaseStatusModel} from "./manual-ui-tree-base-status.model";
import {Path} from "../../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ManualTreeStatusFilterModel} from "./filter/manual-tree-status-filter.model";

export class ManualUiTreeNodeStatusModel extends ManualUiTreeBaseStatusModel {

    path: Path;
    name: string;
    status: ManualTestStatus;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: ManualTestStatus) {
        super(parentContainer, path, name, status);
    }
}
