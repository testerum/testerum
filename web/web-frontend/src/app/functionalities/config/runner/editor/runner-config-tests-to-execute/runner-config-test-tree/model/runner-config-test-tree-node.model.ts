import {RunnerConfigTestTreeBaseModel} from "./runner-config-test-tree-base.model";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunnerConfigTestTreeNodeStatusEnum} from "./enum/runner-config-test-tree-node-status.enum";

export class RunnerConfigTestTreeNodeModel extends RunnerConfigTestTreeBaseModel {

    path: Path;
    name: string;
    status: RunnerConfigTestTreeNodeStatusEnum = RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: RunnerConfigTestTreeNodeStatusEnum) {
        super(parentContainer, path, name, status);
    }
}
