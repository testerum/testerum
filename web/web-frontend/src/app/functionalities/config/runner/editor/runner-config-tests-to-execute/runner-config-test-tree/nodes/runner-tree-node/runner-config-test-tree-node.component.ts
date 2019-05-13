import {Component, Input, OnDestroy} from '@angular/core';
import {RunnerConfigTestTreeBaseModel} from "../../model/runner-config-test-tree-base.model";
import {RunnerConfigTestTreeComponentService} from "../../runner-config-test-tree.component-service";
import {RunnerConfigTestTreeService} from "../../runner-config-test-tree.service";
import {Subscription} from "rxjs";
import {RunnerConfigTestTreeNodeModel} from "../../model/runner-config-test-tree-node.model";
import {RunnerConfigTestTreeContainerModel} from "../../model/runner-config-test-tree-container.model";
import {ModelComponentMapping} from "../../../../../../../../model/infrastructure/model-component-mapping.model";
import {UrlService} from "../../../../../../../../service/url.service";
import {ManualTestStatus} from "../../../../../../../manual/plans/model/enums/manual-test-status.enum";
import {RunnerConfigTestTreeNodeStatusEnum} from "../../model/enum/runner-config-test-tree-node-status.enum";
import {SelectionStateEnum} from "../../../../../../../manual/plans/editor/manual-select-tests-tree/model/enum/selection-state.enum";
import {ManualSelectTestsTreeContainerModel} from "../../../../../../../manual/plans/editor/manual-select-tests-tree/model/manual-select-tests-tree-container.model";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'runner-config-test-tree-node.component.html',
    styleUrls:[
        'runner-config-test-tree-node.component.scss',
        '../../../../../../../../generic/css/tree.scss'
    ]
})
export class RunnerConfigTestTreeNodeComponent implements OnDestroy {

    @Input() model: RunnerConfigTestTreeBaseModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    RunnerConfigTestTreeNodeStatusEnum = RunnerConfigTestTreeNodeStatusEnum;

    constructor(private treeComponentService: RunnerConfigTestTreeComponentService,
                private manualTestsStatusTreeService: RunnerConfigTestTreeService,
                private urlService: UrlService){}

    treeFilterSubscription: Subscription;

    ngOnDestroy(): void {
        if (this.treeFilterSubscription != null) {
            this.treeFilterSubscription.unsubscribe();
        }
    }

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
        this.model.status = this.model.status != RunnerConfigTestTreeNodeStatusEnum.SELECTED ? RunnerConfigTestTreeNodeStatusEnum.SELECTED : RunnerConfigTestTreeNodeStatusEnum.NOT_SELECTED ;

        if(this.model.parentContainer instanceof RunnerConfigTestTreeContainerModel) {
            this.model.parentContainer.calculateCheckState();
        }

        this.selectOrNotChildren(this.model);
    }

    private selectOrNotChildren(node: RunnerConfigTestTreeBaseModel) {
        if (!(node instanceof RunnerConfigTestTreeContainerModel)) { return; }

        let container: RunnerConfigTestTreeContainerModel = node as RunnerConfigTestTreeContainerModel;
        container.status = this.model.status;
        this.model.setSelected(!this.model.isSelected());

        for (let child of container.children) {
            if (child.isContainer()) {
                this.selectOrNotChildren(child as RunnerConfigTestTreeBaseModel)
            } else {
                child.setSelected(this.model.status == RunnerConfigTestTreeNodeStatusEnum.SELECTED);
                child.status = this.model.status;
            }
        }
    }
}
