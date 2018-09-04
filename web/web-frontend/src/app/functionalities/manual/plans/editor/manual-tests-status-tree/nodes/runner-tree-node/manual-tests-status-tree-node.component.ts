import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {ManualUiTreeBaseStatusModel} from "../../model/manual-ui-tree-base-status.model";
import {ModelComponentMapping} from "../../../../../../../model/infrastructure/model-component-mapping.model";
import {ManualTestsStatusTreeComponentService} from "../../manual-tests-status-tree.component-service";
import {ManualTestsStatusTreeService} from "../../manual-tests-status-tree.service";
import {Subscription} from "rxjs";
import {ManualUiTreeNodeStatusModel} from "../../model/manual-ui-tree-node-status.model";
import {ManualTreeStatusFilterModel} from "../../model/filter/manual-tree-status-filter.model";
import {ManualUiTreeContainerStatusModel} from "../../model/manual-ui-tree-container-status.model";
import {ManualTestStatus} from "../../../../model/enums/manual-test-status.enum";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'manual-tests-status-tree-node.component.html',
    styleUrls:[
        'manual-tests-status-tree-node.component.scss',
        '../../../../../../generic/css/tree.scss'
    ]
})
export class ManualTestsStatusTreeNodeComponent implements OnInit, OnDestroy {

    @Input() model:ManualUiTreeBaseStatusModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    isSelected:boolean = false;

    ManualTestStatus = ManualTestStatus;

    constructor(private treeComponentService: ManualTestsStatusTreeComponentService,
                private manualTestsStatusTreeService: ManualTestsStatusTreeService){}

    nodeSelectedSubscription: Subscription;
    treeFilterSubscription: Subscription;
    ngOnInit(): void {
        this.nodeSelectedSubscription = this.treeComponentService.selectedRunnerTreeNodeObserver.subscribe((selectedTreeNode: ManualUiTreeNodeStatusModel) => {
            this.isSelected = this.model.path.equals(selectedTreeNode.path);
        });
        this.treeFilterSubscription = this.manualTestsStatusTreeService.treeFilterObservable.subscribe((filter: ManualTreeStatusFilterModel) => {
            this.model.calculateNodeVisibilityBasedOnFilter(filter)
        });
    }

    ngOnDestroy(): void {
        if (this.nodeSelectedSubscription != null) {
            this.nodeSelectedSubscription.unsubscribe();
        }
        if (this.treeFilterSubscription != null) {
            this.treeFilterSubscription.unsubscribe();
        }
    }

    isFeatureNode(): boolean {
        return this.model instanceof ManualUiTreeContainerStatusModel;
    }

    isTestNode(): boolean {
        return this.model instanceof ManualUiTreeNodeStatusModel;
    }

    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    getStatusTooltip(): string {
        switch (this.model.status) {
            case ManualTestStatus.NOT_EXECUTED: return "Not Executed";
            case ManualTestStatus.IN_PROGRESS: return "In Progress";
            case ManualTestStatus.PASSED: return "Passed";
            case ManualTestStatus.FAILED: return "Failed";
            case ManualTestStatus.BLOCKED: return "Blocked";
            case ManualTestStatus.NOT_APPLICABLE: return "Not Applicable";

            default: return "";
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    setSelected() {
        this.treeComponentService.setNodeAsSelected(this.model);
    }
}
