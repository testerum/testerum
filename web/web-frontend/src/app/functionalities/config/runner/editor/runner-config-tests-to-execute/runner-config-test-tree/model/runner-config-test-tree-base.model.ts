import {JsonTreeNodeAbstract} from "../../../../../../../generic/components/json-tree/model/json-tree-node.abstract";
import {Path} from "../../../../../../../model/infrastructure/path/path.model";
import {JsonTreeNodeState} from "../../../../../../../generic/components/json-tree/model/json-tree-node-state.model";
import {JsonTreeContainer} from "../../../../../../../generic/components/json-tree/model/json-tree-container.model";
import {RunnerConfigTestTreeNodeStatusEnum} from "./enum/runner-config-test-tree-node-status.enum";
import {RunnerConfigTestTreeContainerModel} from "./runner-config-test-tree-container.model";

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

    setSelected(isSelected: boolean) {
        this.selected = isSelected;
        this.status = this.status != RunnerConfigTestTreeNodeStatusEnum.SELECTED ? RunnerConfigTestTreeNodeStatusEnum.SELECTED : RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED ;

        if(this.parentContainer instanceof RunnerConfigTestTreeContainerModel) {
            this.parentContainer.calculateCheckState();
        }

        this.selectOrNotChildren(this);
    }

    private selectOrNotChildren(node: RunnerConfigTestTreeBaseModel) {
        if (!(node instanceof RunnerConfigTestTreeContainerModel)) { return; }

        let container: RunnerConfigTestTreeContainerModel = node as RunnerConfigTestTreeContainerModel;
        container.status = this.status;
        this.selected = !this.isSelected();

        for (let child of container.children) {
            if (child.isContainer()) {
                this.selectOrNotChildren(child as RunnerConfigTestTreeBaseModel)
            } else {
                child.selected = this.status == RunnerConfigTestTreeNodeStatusEnum.SELECTED;
                child.status = this.status;
            }
        }
    }
}
