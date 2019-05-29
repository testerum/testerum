import {Component, Input} from '@angular/core';
import {RunConfigTestTreeBaseModel} from "../../model/run-config-test-tree-base.model";
import {RunConfigTestTreeService} from "../../run-config-test-tree.service";
import {RunConfigTestTreeNodeModel} from "../../model/run-config-test-tree-node.model";
import {RunConfigTestTreeContainerModel} from "../../model/run-config-test-tree-container.model";
import {ModelComponentMapping} from "../../../../../../../../model/infrastructure/model-component-mapping.model";
import {RunConfigTestTreeNodeStatusEnum} from "../../model/enum/run-config-test-tree-node-status.enum";

@Component({
    moduleId: module.id,
    selector: 'run-tree-node',
    templateUrl: 'run-config-test-tree-node.component.html',
    styleUrls:[
        'run-config-test-tree-node.component.scss',
        '../../../../../../../../generic/css/tree.scss'
    ]
})
export class RunConfigTestTreeNodeComponent {

    @Input() model: RunConfigTestTreeBaseModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    RunnerConfigTestTreeNodeStatusEnum = RunConfigTestTreeNodeStatusEnum;

    constructor(private runnerConfigTestTreeService: RunConfigTestTreeService){}

    isFeatureNode(): boolean {
        return this.model instanceof RunConfigTestTreeContainerModel;
    }

    isTestNode(): boolean {
        return this.model instanceof RunConfigTestTreeNodeModel;
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    getStatusTooltip(): string {
        switch (this.model.status) {
            case RunConfigTestTreeNodeStatusEnum.SELECTED: return "Not Selected";
            case RunConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED: return "Partially selected";
            case RunConfigTestTreeNodeStatusEnum.NOT_SELECTED: return "Not selected";

            default: return "";
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    selectOrNot() {
        this.model.setSelected(!this.model.isSelected());
        this.runnerConfigTestTreeService.recalculatePathsFromTreeModel();
    }
}
