import {Component, Input, OnDestroy} from '@angular/core';
import {RunnerConfigTestTreeBaseModel} from "../../model/runner-config-test-tree-base.model";
import {RunnerConfigTestTreeService} from "../../runner-config-test-tree.service";
import {Subscription} from "rxjs";
import {RunnerConfigTestTreeNodeModel} from "../../model/runner-config-test-tree-node.model";
import {RunnerConfigTestTreeContainerModel} from "../../model/runner-config-test-tree-container.model";
import {ModelComponentMapping} from "../../../../../../../../model/infrastructure/model-component-mapping.model";
import {RunnerConfigTestTreeNodeStatusEnum} from "../../model/enum/runner-config-test-tree-node-status.enum";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'runner-config-test-tree-node.component.html',
    styleUrls:[
        'runner-config-test-tree-node.component.scss',
        '../../../../../../../../generic/css/tree.scss'
    ]
})
export class RunnerConfigTestTreeNodeComponent {

    @Input() model: RunnerConfigTestTreeBaseModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    RunnerConfigTestTreeNodeStatusEnum = RunnerConfigTestTreeNodeStatusEnum;

    constructor(private runnerConfigTestTreeService: RunnerConfigTestTreeService){}

    isFeatureNode(): boolean {
        return this.model instanceof RunnerConfigTestTreeContainerModel;
    }

    isTestNode(): boolean {
        return this.model instanceof RunnerConfigTestTreeNodeModel;
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    getStatusTooltip(): string {
        switch (this.model.status) {
            case RunnerConfigTestTreeNodeStatusEnum.SELECTED: return "Not Selected";
            case RunnerConfigTestTreeNodeStatusEnum.PARTIAL_SELECTED: return "Partially selected";
            case RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED: return "Not selected";

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
