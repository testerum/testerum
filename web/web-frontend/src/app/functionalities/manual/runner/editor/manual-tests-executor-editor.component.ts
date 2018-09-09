// import {Component, OnInit} from '@angular/core';
// import {OldManualTestStatus} from "../../model/enums/manual-test-status.enum";
// import {StepPhaseEnum} from "../../../model/enums/step-phase.enum";
// import {ActivatedRoute, Router} from "@angular/router";
// import {ManualTestsExecutorTreeService} from "../tree/manual-tests-executor-tree.service";
// import {Path} from "../../../model/infrastructure/path/path.model";
// import {ManualTestsRunner} from "../../runner/model/manual-tests-runner.model";
// import {OldManualTestStepStatus} from "../../model/enums/manual-test-step-status.enum";
// import {ManualTestsExecutorEditorResolver} from "./manual-tests-executor-editor.resolver";
// import {ManualTestsRunnerService} from "../../runner/service/manual-tests-runner.service";
// import {UpdateManualTestExecutionModel} from "../../runner/model/operation/update-manual-test-execution.model";
// import {ManualTestExeModel} from "../../runner/model/manual-test-exe.model";
// import {ManualTestsRunnerStatus} from "../../runner/model/enums/manual-tests-runner-status.enum";
//
// @Component({
//     selector: 'manual-tests-executor-editor',
//     templateUrl: 'manual-tests-executor-editor.component.html',
//     styleUrls: ['manual-tests-executor-editor.component.scss']
//
// })
// export class ManualTestsExecutorEditorComponent implements OnInit {
//
//     ManualTestStatus = OldManualTestStatus;
//     StepPhaseEnum = StepPhaseEnum;
//
//     manualTestExeModel: ManualTestExeModel = new ManualTestExeModel;
//
//     manualTestRunner: ManualTestsRunner;
//     manualTestRunnerPath: Path;
//     hasStateChanged = false;
//
//     testStatusDropdownOptions = [
//         {label:'Not Executed', value: OldManualTestStatus.NOT_EXECUTED},
//         {label:'Passed', value: OldManualTestStatus.PASSED},
//         {label:'Failed', value: OldManualTestStatus.FAILED},
//         {label:'Blocked', value: OldManualTestStatus.BLOCKED},
//         {label:'Not Applicable', value: OldManualTestStatus.NOT_APPLICABLE},
//     ];
//
//     constructor(private router: Router,
//                 private route: ActivatedRoute,
//                 private manualTestsExecutorEditorResolver:ManualTestsExecutorEditorResolver,
//                 private manualTestsExecutorTreeService: ManualTestsExecutorTreeService,
//                 private manualTestsRunnerService: ManualTestsRunnerService) {
//     }
//
//     ngOnInit(): void {
//         this.route.data.subscribe(data => {
//             let manualTestRunner: ManualTestsRunner = data['manualTestRunner'];
//             this.init(manualTestRunner);
//
//         });
//     }
//
//     private init(manualTestRunner: ManualTestsRunner) {
//         this.manualTestRunner = manualTestRunner;
//         this.manualTestRunnerPath = manualTestRunner.path;
//         let testPathAsString = this.route.snapshot.params["testPath"];
//         let testPath = Path.createInstance(testPathAsString);
//         for (const test of manualTestRunner.testsToExecute) {
//             if (test.path.equals(testPath)) {
//                 this.manualTestExeModel = test;
//                 break;
//             }
//         }
//     }
//
//     stepStatusChanged(stepStatusEnum: any, stepIndex: number) {
//         this.hasStateChanged = true;
//
//         for (let i = 0; i < stepIndex; i++) {
//             let testStep = this.manualTestExeModel.steps[i];
//             if (testStep.stepStatus == OldManualTestStepStatus.NOT_EXECUTED ) {
//                 testStep.stepStatus = OldManualTestStepStatus.PASSED;
//             }
//         }
//
//         if (stepStatusEnum == OldManualTestStepStatus.FAILED) {
//             this.manualTestExeModel.testStatus = OldManualTestStatus.FAILED;
//         }
//
//         let allTestsArePassed = true;
//         for (const testStep of this.manualTestExeModel.steps) {
//             if(testStep.stepStatus != OldManualTestStepStatus.PASSED) {
//                 allTestsArePassed = false;
//                 break;
//             }
//         }
//         if (allTestsArePassed) {
//             this.manualTestExeModel.testStatus = OldManualTestStatus.PASSED;
//         }
//     }
//
//     onTestChange() {
//         this.hasStateChanged = true;
//     }
//
//     getPhaseEnumValues(): Array<StepPhaseEnum> {
//         return [StepPhaseEnum.GIVEN, StepPhaseEnum.WHEN, StepPhaseEnum.THEN]
//     }
//
//     isTestSuiteFinalized(): boolean {
//         return this.manualTestRunner.status == ManualTestsRunnerStatus.FINISHED
//     }
//     getTestPathDirectoryAsString(): string {
//         if(this.manualTestExeModel.path == null || this.manualTestExeModel.path.directories.length == 0) return "/";
//         return this.manualTestExeModel.path.toDirectoryString()
//     }
//
//     getPhaseText(phase: StepPhaseEnum, stepIndex: number) {
//         if(stepIndex == 0) {
//             return StepPhaseEnum[phase];
//         }
//
//         if (this.manualTestExeModel.steps[stepIndex-1].phase == phase) {
//             return StepPhaseEnum[StepPhaseEnum.AND];
//         }
//
//         return StepPhaseEnum[phase];
//     }
//
//     resetChanges(): void {
//         this.manualTestsExecutorEditorResolver.resolve(this.route.snapshot).subscribe(
//             manualTestRunner => this.init(manualTestRunner)
//         );
//     }
//
//     saveAction(): void {
//         let updateManualTestRunner = new UpdateManualTestExecutionModel(this.manualTestRunnerPath, this.manualTestExeModel);
//         this.manualTestsRunnerService
//             .updateExecutedTest(updateManualTestRunner)
//             .subscribe(manualTestRunner => this.afterSaveHandler(manualTestRunner));
//     }
//
//     private afterSaveHandler(manualTestRunner: ManualTestsRunner) {
//         this.hasStateChanged = false;
//         this.init(manualTestRunner);
//         this.manualTestsExecutorTreeService.initializeTestsTreeFromServer(this.manualTestRunnerPath);
//     }
//
//     private showNextUnexecutedTest(): void {
//         let nextManualTestToExecute = this.manualTestsExecutorTreeService.getNextUnExecutedTest(this.manualTestExeModel.path);
//
//         if (nextManualTestToExecute) {
//
//             this.router.navigate(["../test", {
//                     runnerPath: this.manualTestsExecutorTreeService.manualTestRunner.path.toString(),
//                     testPath: nextManualTestToExecute.path.toString()
//                 }],
//                 {
//                     relativeTo: this.route,
//                     queryParamsHandling: 'preserve',
//                     preserveFragment: true
//                 }
//             );
//         }
//     }
// }
