import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {ManualTestsRunnerService} from "../service/manual-tests-runner.service";
import {ManualTestsRunner} from "../model/manual-tests-runner.model";
import {SelectTestsTreeRunnerService} from "./select-tests-tree/select-tests-tree-runner.service";
import {ModelComponentMapping} from "../../../model/infrastructure/model-component-mapping.model";
import {SelectTestTreeRunnerContainerModel} from "./select-tests-tree/model/select-test-tree-runner-container.model";
import {SelectTestTreeRunnerContainerComponent} from "./select-tests-tree/container/select-test-tree-runner-container.component";
import {SelectTestTreeRunnerNodeModel} from "./select-tests-tree/model/select-test-tree-runner-node.model";
import {SelectTestTreeRunnerNodeComponent} from "./select-tests-tree/container/node/select-test-tree-runner-node.component";
import {ManualTestsTreeExecutorContainerModel} from "../../executer/tree/model/manual-tests-tree-executor-container.model";
import {ManualTestsExecutorTreeContainerComponent} from "../../executer/tree/container/manual-tests-executor-tree-container.component";
import {ManualTestsTreeExecutorNodeModel} from "../../executer/tree/model/manual-tests-tree-executor-node.model";
import {ManualTestsExecutorTreeService} from "../../executer/tree/manual-tests-executor-tree.service";
import {ManualTestsExecutorTreeNodeComponent} from "../../executer/tree/container/node/manual-tests-executor-tree-node.component";
import {ManualTestsOverviewService} from "../overview/manual-tests-overview.service";
import {ManualTestsRunnerStatus} from "../model/enums/manual-tests-runner-status.enum";
import {AreYouSureModalEnum} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {AreYouSureModalService} from "../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";

@Component({
    selector: 'manual-test-runner-editor',
    templateUrl: 'manual-tests-runner-editor.component.html',
    styleUrls: ['manual-tests-runner-editor.component.scss']
})
export class ManualTestsRunnerEditorComponent implements OnInit {

    manualTestRunner: ManualTestsRunner = new ManualTestsRunner();
    isEditExistingTest: boolean; //TODO: is this used?
    isEditMode: boolean = false;
    isFinalized: boolean = false;
    isCreateAction: boolean = false;

    pieChartData: any;

    selectTreeComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(SelectTestTreeRunnerContainerModel, SelectTestTreeRunnerContainerComponent)
        .addPair(SelectTestTreeRunnerNodeModel, SelectTestTreeRunnerNodeComponent);

    executorTestsComponentMapping: ModelComponentMapping = new ModelComponentMapping()
        .addPair(ManualTestsTreeExecutorContainerModel, ManualTestsExecutorTreeContainerComponent)
        .addPair(ManualTestsTreeExecutorNodeModel, ManualTestsExecutorTreeNodeComponent);

    constructor(private router: Router,
                private route: ActivatedRoute,
                private manualTestsRunnerService: ManualTestsRunnerService,
                private manualTestsOverviewService: ManualTestsOverviewService,
                private areYouSureModalService: AreYouSureModalService,
                public selectTestsTreeRunnerService: SelectTestsTreeRunnerService,
                public manualTestsExecutorTreeService: ManualTestsExecutorTreeService,) {
    }

    ngOnInit(): void {

        this.route.data.subscribe(data => {
            let manualTestsRunner = data['manualTestRunner'];

            this.pieChartData = {
                labels: ['Passed Tests', 'Failed Tests', 'Blocked Tests', "Not Applicable Tests", "Not Executed"],
                datasets: [{
                    data: [
                        manualTestsRunner.passedTests,
                        manualTestsRunner.failedTests,
                        manualTestsRunner.blockedTests,
                        manualTestsRunner.notApplicableTests,
                        manualTestsRunner.notExecutedTests
                    ],
                    backgroundColor: ["#5cb85c", "#FF6384", "#FFCE56", "#36A2EB", "#c0bebc"],
                    hoverBackgroundColor: ["#5cb85c", "#FF6384", "#FFCE56", "#36A2EB", "#c0bebc"]
                }]
            };

            this.initialize(manualTestsRunner);
        });
    }

    private initialize(manualTestsRunner: ManualTestsRunner) {
        this.manualTestRunner = manualTestsRunner;

        this.isEditExistingTest = !this.manualTestRunner.path.isEmpty();
        this.setEditMode(this.manualTestRunner.path.isEmpty());
        this.isFinalized = manualTestsRunner.status == ManualTestsRunnerStatus.FINISHED;

        this.isCreateAction = this.manualTestRunner.path.isEmpty();

        this.selectTestsTreeRunnerService.initializeTestsTree(this.manualTestRunner);
        this.manualTestsExecutorTreeService.initializeTestsTree(this.manualTestRunner);

        this.pieChartData.datasets[0].data[0] = this.manualTestRunner.passedTests;
        this.pieChartData.datasets[0].data[1] = this.manualTestRunner.failedTests;
        this.pieChartData.datasets[0].data[2] = this.manualTestRunner.blockedTests;
        this.pieChartData.datasets[0].data[3] = this.manualTestRunner.notApplicableTests;
        this.pieChartData.datasets[0].data[4] = this.manualTestRunner.notExecutedTests
    }

    setEditMode(editMode: boolean) {
        this.isEditMode = editMode;
        this.selectTestsTreeRunnerService.isEditMode = editMode;
    }

    enableEditTestMode(): void {
        this.setEditMode(true);
    }

    navigateToExecutorMode() {
        this.router.navigate(["manual/execute", {runnerPath : this.manualTestRunner.path.toString()} ]);
    }

    cancelAction(): void {
        if (this.isCreateAction) {
            this.router.navigate(["manual/tests"]);
        } else {
            this.manualTestsRunnerService.getTestRunner(this.manualTestRunner.path.toString()).subscribe(
                (result: ManualTestsRunner) => {
                    this.initialize(result);
                    this.setEditMode(false);
                }
            )
        }
    }

    deleteAction(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Delete Runner",
            "Are you sure you want to delete this Manual Tests Runner?")
            .subscribe((action: AreYouSureModalEnum) => {
                if (action == AreYouSureModalEnum.OK) {
                    this.manualTestsRunnerService.delete(this.manualTestRunner).subscribe(restul => {
                        this.manualTestsOverviewService.initializeRunnersOverview();
                        this.router.navigate(["/manual/runner"]);
                    });
                }
            });
    }

    finalize(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Finalize Execution",
            "Are you sure you want to finalize this Tests Execution?")
            .subscribe((action: AreYouSureModalEnum) => {
                if (action == AreYouSureModalEnum.OK) {
                    this.manualTestsRunnerService
                        .finalize(this.manualTestRunner)
                        .subscribe(savedModel => this.afterSaveHandler(savedModel));
                }
            });
    }

    bringBackInExecution():void {
        this.manualTestsRunnerService
            .bringBackInExecution(this.manualTestRunner)
            .subscribe(savedModel => this.afterSaveHandler(savedModel));
    }

    saveAction(): void {
        this.manualTestRunner.testsToExecute = this.selectTestsTreeRunnerService.getSelectedTests();

        this.manualTestsRunnerService
            .save(this.manualTestRunner)
            .subscribe(savedModel => this.afterSaveHandler(savedModel));
    }

    private afterSaveHandler(savedManualTestRunner: ManualTestsRunner) {
        this.initialize(savedManualTestRunner);
        this.manualTestsOverviewService.initializeRunnersOverview();
        this.setEditMode(false);
        this.router.navigate(["/manual/runner/show", {path : savedManualTestRunner.path.toString()} ]);
    }

}
