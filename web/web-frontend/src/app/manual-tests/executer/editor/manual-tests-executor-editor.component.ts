import {Component, OnInit} from '@angular/core';
import {ManualTestStatus} from "../../model/enums/manual-test-status.enum";
import {UpdateManualTestModel} from "../../model/operation/update-manual-test.model";
import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
import {ActivatedRoute, Router} from "@angular/router";
import {ManualTestsExecutorTreeService} from "../tree/manual-tests-executor-tree.service";
import {Path} from "../../../model/infrastructure/path/path.model";
import {ManualTestsRunner} from "../../runner/model/manual-tests-runner.model";
import {ManualTestStepStatus} from "../../model/enums/manual-test-step-status.enum";
import {ManualTestsExecutorEditorResolver} from "./manual-tests-executor-editor.resolver";
import {ManualTestsRunnerService} from "../../runner/service/manual-tests-runner.service";
import {UpdateManualTestExecutionModel} from "../../runner/model/operation/update-manual-test-execution.model";
import {ManualTestExeModel} from "../../runner/model/manual-test-exe.model";
import {ManualTestsRunnerStatus} from "../../runner/model/enums/manual-tests-runner-status.enum";

@Component({
    selector: 'manual-tests-executor-editor',
    templateUrl: 'manual-tests-executor-editor.component.html',
    styleUrls: ['manual-tests-executor-editor.component.scss', '../../../generic/css/generic.scss', '../../../generic/css/forms.scss']

})

export class ManualTestsExecutorEditorComponent implements OnInit {

    ManualTestStatus = ManualTestStatus;
    StepPhaseEnum = StepPhaseEnum;

    manualTestExeModel: ManualTestExeModel = new ManualTestExeModel;

    manualTestRunner: ManualTestsRunner;
    manualTestRunnerPath: Path;
    hasStateChanged = false;

    testStatusDropdownOptions = [
        {label:'Not Executed', value: ManualTestStatus.NOT_EXECUTED},
        {label:'Passed', value: ManualTestStatus.PASSED},
        {label:'Failed', value: ManualTestStatus.FAILED},
        {label:'Blocked', value: ManualTestStatus.BLOCKED},
        {label:'Not Applicable', value: ManualTestStatus.NOT_APPLICABLE},
    ];

    constructor(private router: Router,
                private route: ActivatedRoute,
                private manualTestsExecutorEditorResolver:ManualTestsExecutorEditorResolver,
                private manualTestsExecutorTreeService: ManualTestsExecutorTreeService,
                private manualTestsRunnerService: ManualTestsRunnerService) {
    }

    ngOnInit(): void {
        this.route.data.subscribe(data => {
            let manualTestRunner: ManualTestsRunner = data['manualTestRunner'];
            this.init(manualTestRunner);

        });
    }

    private init(manualTestRunner: ManualTestsRunner) {
        this.manualTestRunner = manualTestRunner;
        this.manualTestRunnerPath = manualTestRunner.path;
        let testPathAsString = this.route.snapshot.params["testPath"];
        let testPath = Path.createInstance(testPathAsString);
        for (const test of manualTestRunner.testsToExecute) {
            if (test.path.equals(testPath)) {
                this.manualTestExeModel = test;
                break;
            }
        }
    }

    stepStatusChanged(stepStatusEnum: any, stepIndex: number) {
        this.hasStateChanged = true;

        for (let i = 0; i < stepIndex; i++) {
            let testStep = this.manualTestExeModel.steps[i];
            if (testStep.stepStatus == ManualTestStepStatus.NOT_EXECUTED ) {
                testStep.stepStatus = ManualTestStepStatus.PASSED;
            }
        }

        if (stepStatusEnum == ManualTestStepStatus.FAILED) {
            this.manualTestExeModel.testStatus = ManualTestStatus.FAILED;
        }

        let allTestsArePassed = true;
        for (const testStep of this.manualTestExeModel.steps) {
            if(testStep.stepStatus != ManualTestStepStatus.PASSED) {
                allTestsArePassed = false;
                break;
            }
        }
        if (allTestsArePassed) {
            this.manualTestExeModel.testStatus = ManualTestStatus.PASSED;
        }
    }

    onTestChange() {
        this.hasStateChanged = true;
    }

    getPhaseEnumValues(): Array<StepPhaseEnum> {
        return [StepPhaseEnum.GIVEN, StepPhaseEnum.WHEN, StepPhaseEnum.THEN]
    }

    isTestSuiteFinalized(): boolean {
        return this.manualTestRunner.status == ManualTestsRunnerStatus.FINISHED
    }
    getTestPathDirectoryAsString(): string {
        if(this.manualTestExeModel.path == null || this.manualTestExeModel.path.directories.length == 0) return "/";
        return this.manualTestExeModel.path.toDirectoryString()
    }

    getPhaseText(phase: StepPhaseEnum, stepIndex: number) {
        if(stepIndex == 0) {
            return StepPhaseEnum[phase];
        }

        if (this.manualTestExeModel.steps[stepIndex-1].phase == phase) {
            return StepPhaseEnum[StepPhaseEnum.AND];
        }

        return StepPhaseEnum[phase];
    }

    resetChanges(): void {
        this.manualTestsExecutorEditorResolver.resolve(this.route.snapshot).subscribe(
            manualTestRunner => this.init(manualTestRunner)
        );
    }

    saveAction(): void {
        let updateManualTestModel = new UpdateManualTestModel(
            this.manualTestExeModel.path,
            this.manualTestExeModel
        );

        this.manualTestsRunnerService
            let updateManualTestRunner = new UpdateManualTestExecutionModel(this.manualTestRunnerPath, this.manualTestExeModel);
            this.manualTestsRunnerService
            .updateExecutedTest(updateManualTestRunner)
            .subscribe(manualTestRunner => this.afterSaveHandler(manualTestRunner));
    }

    private afterSaveHandler(manualTestRunner: ManualTestsRunner) {
        this.hasStateChanged = false;
        this.init(manualTestRunner);
        this.manualTestsExecutorTreeService.initializeTestsTreeFromServer(this.manualTestRunnerPath);
    }

    private showNextUnexecutedTest(): void {
        let nextManualTestToExecute = this.manualTestsExecutorTreeService.getNextUnExecutedTest(this.manualTestExeModel.path);

        if (nextManualTestToExecute) {

            this.router.navigate(["../test", {
                    runnerPath: this.manualTestsExecutorTreeService.manualTestRunner.path.toString(),
                    testPath: nextManualTestToExecute.path.toString()
                }],
                {
                    relativeTo: this.route,
                    queryParamsHandling: 'preserve',
                    preserveFragment: true
                }
            );
        }
    }
}
