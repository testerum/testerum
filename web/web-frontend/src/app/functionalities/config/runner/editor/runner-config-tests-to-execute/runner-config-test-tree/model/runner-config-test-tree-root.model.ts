import {RunnerConfigTestTreeContainerModel} from "./runner-config-test-tree-container.model";
import {RunnerConfigTestTreeBaseModel} from "./runner-config-test-tree-base.model";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {ManualTestStatus} from "../../../../../../manual/plans/model/enums/manual-test-status.enum";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunnerConfigTestTreeNodeStatusEnum} from "./enum/runner-config-test-tree-node-status.enum";

export class RunnerConfigTestTreeRootModel extends RunnerConfigTestTreeContainerModel {

    path: Path;
    name: string;
    status: RunnerConfigTestTreeNodeStatusEnum = RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    children: Array<RunnerConfigTestTreeBaseModel> = [];
}
