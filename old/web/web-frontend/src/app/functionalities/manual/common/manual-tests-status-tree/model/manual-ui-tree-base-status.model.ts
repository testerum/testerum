import {JsonTreeNodeAbstract} from "../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../plans/model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../generic/components/json-tree/model/json-tree-container.model";
import {ManualTreeStatusFilterModel} from "./filter/manual-tree-status-filter.model";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {ManualUiTreeContainerStatusModel} from "./manual-ui-tree-container-status.model";

export abstract class ManualUiTreeBaseStatusModel extends JsonTreeNodeAbstract {

    path: Path;
    name: string;
    status: ManualTestStatus = ManualTestStatus.NOT_EXECUTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: ManualTestStatus) {
        super(parentContainer);
        this.path = path;
        this.name = name;
        this.status = status;
    }
}
