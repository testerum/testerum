import {JsonTreeNodeAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../../../../manual/plans/model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";

export abstract class RunnerConfigTestTreeBaseModel extends JsonTreeNodeAbstract {

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
