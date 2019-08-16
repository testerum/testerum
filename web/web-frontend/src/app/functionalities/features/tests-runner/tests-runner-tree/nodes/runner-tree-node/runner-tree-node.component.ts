import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {RunnerTreeNodeModel} from "../../model/runner-tree-node.model";
import {ExecutionStatusEnum} from "../../../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeService} from "../../runner-tree.service";
import {RunnerComposedStepTreeNodeModel} from "../../model/runner-composed-step-tree-node.model";
import {RunnerBasicStepTreeNodeModel} from "../../model/runner-basic-step-tree-node.model";
import {ModelComponentMapping} from "../../../../../../model/infrastructure/model-component-mapping.model";
import {RunnerFeatureTreeNodeModel} from "../../model/runner-feature-tree-node.model";
import {Subscription} from "rxjs";
import {RunnerTestTreeNodeModel} from "../../model/runner-test-tree-node.model";
import {TestsRunnerService} from "../../../tests-runner.service";
import {RunnerTreeFilterModel} from "../../model/filter/runner-tree-filter.model";
import {UrlService} from "../../../../../../service/url.service";
import {RunnerScenarioTreeNodeModel} from "../../model/runner-scenario-tree-node.model";
import {RunnerParametrizedTestTreeNodeModel} from "../../model/runner-parametrized-test-tree-node.model";
import {RunnerRootTreeNodeModel} from "../../model/runner-root-tree-node.model";

@Component({
    moduleId: module.id,
    selector: 'run-tree-node',
    templateUrl: 'runner-tree-node.component.html',
    styleUrls:[
        'runner-tree-node.component.scss',
        '../../../../../../generic/css/tree.scss'
    ]
})
export class RunnerTreeNodeComponent implements OnInit, OnDestroy {

    @Input() model:RunnerTreeNodeModel;
    @Input() modelComponentMapping: ModelComponentMapping;

    hasMouseOver: boolean = false;

    RunnerTreeNodeStateEnum = ExecutionStatusEnum;

    constructor(private runnerTreeComponentService:RunnerTreeService,
                private testsRunnerService: TestsRunnerService,
                private urlService: UrlService){}

    runnerTreeFilterSubscription: Subscription;
    ngOnInit(): void {
        this.runnerTreeFilterSubscription = this.testsRunnerService.treeFilterObservable.subscribe((filter: RunnerTreeFilterModel) => {
            this.model.calculateNodeVisibilityBasedOnFilter(filter)
        });
    }

    ngOnDestroy(): void {
        if (this.runnerTreeFilterSubscription != null) {
            this.runnerTreeFilterSubscription.unsubscribe();
        }
    }

    isTestSuiteNode(): boolean {
        return this.model instanceof RunnerRootTreeNodeModel;
    }
    isFeatureNode(): boolean {
        return this.model instanceof RunnerFeatureTreeNodeModel;
    }
    isTestNode(): boolean {
        return this.model instanceof RunnerTestTreeNodeModel || this.model instanceof RunnerParametrizedTestTreeNodeModel;
    }
    isScenarioNode(): boolean {
        return this.model instanceof RunnerScenarioTreeNodeModel;
    }
    isStepNode(): boolean {
        return this.model instanceof RunnerComposedStepTreeNodeModel || this.model instanceof RunnerBasicStepTreeNodeModel
    }
    isOpenedNode(): boolean {
        return this.model.jsonTreeNodeState.showChildren
    }

    getStatusTooltip(): string {
        let result = "";
        if(this.isTestSuiteNode()) result += "Test Suite";
        if(this.isFeatureNode()) result += "Feature";
        if(this.isTestNode()) result += "Test";
        if(this.isScenarioNode()) result += "Scenario";
        if(this.isStepNode()) result += "Step";

        switch (this.model.state) {
            case ExecutionStatusEnum.WAITING: result += " is Waiting"; break;
            case ExecutionStatusEnum.EXECUTING : result += " is Executing"; break;
            case ExecutionStatusEnum.PASSED: result += " has Passed"; break;
            case ExecutionStatusEnum.FAILED: result += " has Failed"; break;
            case ExecutionStatusEnum.DISABLED: result += " is Disabled"; break;
            case ExecutionStatusEnum.UNDEFINED: result += " is Undefined Steps"; break;
            case ExecutionStatusEnum.SKIPPED: result += " was Skipped"; break;
        }

        return result;
    }

    collapseNode() {
        this.model.jsonTreeNodeState.showChildren = !this.model.jsonTreeNodeState.showChildren
    }

    setSelected() {
        this.runnerTreeComponentService.setNodeAsSelected(this.model);
    }

    onEditTest() {
        this.urlService.navigateToTest(this.model.path);
    }

    isEnabled(): boolean {

        if (this.model instanceof RunnerTestTreeNodeModel || this.model instanceof RunnerParametrizedTestTreeNodeModel) {
            return this.model.enabled;
        }

        if (this.model instanceof RunnerScenarioTreeNodeModel) {
            return this.model.enabled;
        }

        return true;
    }
}
