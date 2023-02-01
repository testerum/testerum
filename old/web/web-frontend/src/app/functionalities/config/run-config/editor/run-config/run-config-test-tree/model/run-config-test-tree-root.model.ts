import {RunConfigTestTreeContainerModel} from "./run-config-test-tree-container.model";
import {RunConfigTestTreeBaseModel} from "./run-config-test-tree-base.model";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunConfigTestTreeNodeStatusEnum} from "./enum/run-config-test-tree-node-status.enum";

export class RunConfigTestTreeRootModel extends RunConfigTestTreeContainerModel {

    path: Path;
    name: string;
    status: RunConfigTestTreeNodeStatusEnum = RunConfigTestTreeNodeStatusEnum.NOT_SELECTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;
    children: Array<RunConfigTestTreeBaseModel> = [];
}
