import {JsonTreeNodeAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunConfigTestTreeNodeStatusEnum} from "./enum/run-config-test-tree-node-status.enum";

export abstract class RunConfigTestTreeBaseModel extends JsonTreeNodeAbstract {

    path: Path;
    name: string;
    status: RunConfigTestTreeNodeStatusEnum = RunConfigTestTreeNodeStatusEnum.NOT_SELECTED;

    jsonTreeNodeState: JsonTreeNodeState = new JsonTreeNodeState();
    parentContainer: JsonTreeContainer;
    hidden: boolean = false;

    constructor(parentContainer: JsonTreeContainer, path: Path, name: string, status: RunConfigTestTreeNodeStatusEnum) {
        super(parentContainer);
        this.path = path;
        this.name = name;
        this.status = status;
    }

    isSelectedNode(): boolean {
        return this.status == RunConfigTestTreeNodeStatusEnum.SELECTED
    }

    isPartialSelectedNode(): boolean {
        return this.status == RunConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED
    }

    isNotSelectedNode(): boolean {
        return this.status == RunConfigTestTreeNodeStatusEnum.NOT_SELECTED
    }

    abstract setSelected(isSelected: boolean);
}
