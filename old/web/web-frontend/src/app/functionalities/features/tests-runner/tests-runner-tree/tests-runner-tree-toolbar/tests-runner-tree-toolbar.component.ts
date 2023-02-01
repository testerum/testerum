import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {TestsRunnerService} from "../../tests-runner.service";
import {RunnerTreeFilterModel} from "../model/filter/runner-tree-filter.model";
import {JsonTreeExpandUtil} from "../../../../../generic/components/json-tree/util/json-tree-expand.util";
import {JsonTreeModel} from "../../../../../generic/components/json-tree/model/json-tree.model";
import {RunnerComposedStepTreeNodeModel} from "../model/runner-composed-step-tree-node.model";
import {RunnerTestTreeNodeModel} from "../model/runner-test-tree-node.model";
import {RunnerTreeService} from "../runner-tree.service";
import {RunConfig} from "../../../../config/run-config/model/runner-config.model";
import {PathWithScenarioIndexes} from "../../../../config/run-config/model/path-with-scenario-indexes.model";

@Component({
    selector: 'tests-runner-tree-toolbar',
    templateUrl: './tests-runner-tree-toolbar.component.html',
    styleUrls: ['./tests-runner-tree-toolbar.component.scss']
})
export class TestsRunnerTreeToolbarComponent implements OnInit {

    @Input() treeModel:JsonTreeModel ;
    @Input() reportMode: boolean = false;

    @Output() stopTests: EventEmitter<string> = new EventEmitter<string>();

    model = new RunnerTreeFilterModel();

    constructor(private testRunnerService: TestsRunnerService,
                private runnerTreeService: RunnerTreeService) {}

    ngOnInit() {}

    onRunOrStopTests() {
        if (this.areTestRunning()) {
            this.stopTests.emit(null);
        } else {
            this.testRunnerService.reRunTests()
        }
    }

    onReRunFailedTests() {
        let runConfig = new RunConfig();
        runConfig.name = "Failed Tests Execution";
        runConfig.settings = this.testRunnerService.lastRunConfig.settings;
        runConfig.pathsToInclude = this.runnerTreeService.getFailedTestsPaths();
        this.testRunnerService.runRunConfig(runConfig);
    }

    areTestRunning(): boolean {
        return this.testRunnerService.areTestRunning;
    }

    shouldShowRerunFailedTestButton(): boolean {
        return !this.testRunnerService.areTestRunning && this.runnerTreeService.hasFailedTests;
    }

    onToggleFolders() {
        this.model.areTestFoldersShown = !this.model.areTestFoldersShown;
        this.testRunnerService.showTestFoldersEventObservable.emit(this.model.areTestFoldersShown);
    }

    onToggleWaiting() {
        this.model.showWaiting = !this.model.showWaiting;
        this.triggerFilterChangeEvent();
    }

    onTogglePassed() {
        this.model.showPassed = !this.model.showPassed;
        this.triggerFilterChangeEvent();
    }

    onToggleFailed() {
        this.model.showFailed = !this.model.showFailed;
        this.triggerFilterChangeEvent();
    }

    onToggleDisabled() {
        this.model.showDisabled = !this.model.showDisabled;
        this.triggerFilterChangeEvent();
    }

    onToggleUndefined() {
        this.model.showUndefined = !this.model.showUndefined;
        this.triggerFilterChangeEvent();
    }

    onToggleSkipped() {
        this.model.showSkipped = !this.model.showSkipped;
        this.triggerFilterChangeEvent();
    }

    private triggerFilterChangeEvent() {
        this.testRunnerService.treeFilterObservable.emit(this.model)
    }

    onExpandAllNodes(): void {
        JsonTreeExpandUtil.expandTreeToLevel(this.treeModel,  100);
    }

    onExpandToTests() {
        JsonTreeExpandUtil.expandTreeToNodeType(this.treeModel,  RunnerTestTreeNodeModel);
    }

    onExpandToSteps() {
        JsonTreeExpandUtil.expandTreeToNodeType(this.treeModel,  RunnerComposedStepTreeNodeModel);
    }
}
