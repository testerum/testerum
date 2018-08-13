import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {RunnerTreeNodeModel} from "../../model/runner-tree-node.model";
import {ExecutionStatusEnum} from "../../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeComponentService} from "../../runner-tree.component-service";
import {RunnerComposedStepTreeNodeModel} from "../../model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "../../model/runner-basic-step-tree-node.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {RunnerFeatureTreeNodeModel} from "../../model/runner-feature-tree-node.model";
import {Subscription} from "rxjs";
import {RunnerTestTreeNodeModel} from "../../model/runner-test-tree-node.model";
import {TestsRunnerService} from "../../../tests-runner.service";
import {RunnerTreeFilterModel} from "../../model/filter/runner-tree-filter.model";

@Component({
    moduleId: module.id,
    selector: 'runner-tree-node',
    templateUrl: 'runner-tree-node.component.html',
    styleUrls:[
        'runner-tree-node.component.scss',
        '../../../../../../generic/css/tree.scss'
    ]
})
export class RunnerTreeNodeComponent implements OnInit, OnDestroy {

    @Input() model:RunnerTreeNodeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    isSelected:boolean = false;

    RunnerTreeNodeStateEnum = ExecutionStatusEnum;

    constructor(private runnerTreeComponentService:RunnerTreeComponentService,
                private testsRunnerService: TestsRunnerService){}

    nodeSelectedSubscription: Subscription;
    runnerTreeFilterSubscription: Subscription;
    ngOnInit(): void {
        this.nodeSelectedSubscription = this.runnerTreeComponentService.selectedRunnerTreeNodeObserver.subscribe((selectedTreeNode: RunnerTreeNodeModel) => {
            this.isSelected = this.model.equals(selectedTreeNode);
        });
        this.runnerTreeFilterSubscription = this.testsRunnerService.treeFilterObservable.subscribe((filter: RunnerTreeFilterModel) => {
            this.onFilterChange(filter)
        });
    }

    ngOnDestroy(): void {
        if (this.nodeSelectedSubscription != null) {
            this.nodeSelectedSubscription.unsubscribe();
        }
        if (this.runnerTreeFilterSubscription != null) {
            this.runnerTreeFilterSubscription.unsubscribe();
        }
    }

    isFeatureNode(): boolean {
        return this.model instanceof RunnerFeatureTreeNodeModel;
    }

    isTestNode(): boolean {
        return this.model instanceof RunnerTestTreeNodeModel;
    }
    isStepNode(): boolean {
        return this.model instanceof RunnerComposedStepTreeNodeModel || this.model instanceof RunnerBasicStepTreeNodeModel
    }
    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    getStatusTooltip(): string {
        switch (this.model.state) {
            case ExecutionStatusEnum.WAITING: return "Waiting";
            case ExecutionStatusEnum.EXECUTING : return "Executing";
            case ExecutionStatusEnum.PASSED: return "Passed";
            case ExecutionStatusEnum.FAILED: return "Failed";
            case ExecutionStatusEnum.ERROR: return "Error";
            case ExecutionStatusEnum.DISABLED: return "Disabled";
            case ExecutionStatusEnum.UNDEFINED: return "Undefined steps";
            case ExecutionStatusEnum.SKIPPED: return "Skipped";
        }
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    setSelected() {
        this.runnerTreeComponentService.setNodeAsSelected(this.model);
    }

    private onFilterChange(filter: RunnerTreeFilterModel) {
        if (!(this.model instanceof RunnerTestTreeNodeModel)) {
            return;
        }

        if (filter.showWaiting == filter.showPassed &&
            filter.showPassed == filter.showFailed &&
            filter.showFailed == filter.showError &&
            filter.showError == filter.showDisabled &&
            filter.showDisabled == filter.showUndefined &&
            filter.showUndefined == filter.showSkipped) {

            this.model.hidden = false;
            return;
        }

        if(this.model.state == ExecutionStatusEnum.WAITING) {this.model.hidden = !filter.showWaiting;}
        if(this.model.state == ExecutionStatusEnum.PASSED) {this.model.hidden = !filter.showPassed;}
        if(this.model.state == ExecutionStatusEnum.FAILED) {this.model.hidden = !filter.showFailed;}
        if(this.model.state == ExecutionStatusEnum.ERROR) {this.model.hidden = !filter.showError;}
        if(this.model.state == ExecutionStatusEnum.DISABLED) {this.model.hidden = !filter.showDisabled;}
        if(this.model.state == ExecutionStatusEnum.UNDEFINED) {this.model.hidden = !filter.showUndefined;}
        if(this.model.state == ExecutionStatusEnum.SKIPPED) {this.model.hidden = !filter.showSkipped;}
    }
}
