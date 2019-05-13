import {JsonTreeNodeAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunnerConfigTestTreeNodeStatusEnum} from "./enum/runner-config-test-tree-node-status.enum";

export abstract class RunnerConfigTestTreeBaseModel extends JsonTreeNodeAbstract {

    path: Path;
    name: string;
    status: RunnerConfigTestTreeNodeStatusEnum = RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: RunnerConfigTestTreeNodeStatusEnum) {
        super(parentContainer);
        this.path = path;
        this.name = name;
        this.status = status;
    }


    isSelectedNode(): boolean {
        return this.status == RunnerConfigTestTreeNodeStatusEnum.SELECTED
    }

    isPartialSelectedNode(): boolean {
        return this.status == RunnerConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED
    }

    isNotSelectedNode(): boolean {
        return this.status == RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED
    }
}
