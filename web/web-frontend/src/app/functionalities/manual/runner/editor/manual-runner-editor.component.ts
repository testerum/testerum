import {Component, OnInit} from '@angular/core';
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {ManualTestStatus} from "../../plans/model/enums/manual-test-status.enum";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ActivatedRoute, Router} from "@angular/router";
import {ManualExecPlansService} from "../../service/manual-exec-plans.service";
import {ManualTest} from "../../plans/model/manual-test.model";
import {ManualTestStepStatus} from "../../plans/model/enums/manual-test-step-status.enum";
import {UrlService} from "../../../../service/url.service";

@Component({
    selector: 'manual-runner-editor',
    templateUrl: 'manual-runner-editor.component.html',
    styleUrls: ['manual-runner-editor.component.scss']
})
export class ManualRunnerEditorComponent implements OnInit {

    ManualTestStatus = ManualTestStatus;
    StepPhaseEnum = StepPhaseEnum;

    model: ManualTest;
    path: Path;
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
                private manualExecPlansService: ManualExecPlansService,
                private urlService: UrlService) {
    }

    ngOnInit(): void {
        this.hasStateChanged = false;
        
        let pathAsString = this.route.snapshot.params["path"];
        this.path = pathAsString ? Path.createInstance(pathAsString) : null;

        if (this.path) {
            this.manualExecPlansService.getManualTest(this.path).subscribe((manualTest: ManualTest) => {
                this.model = manualTest;
            });
        } else {
            this.model = null;
        }
    }

    stepStatusChanged(stepStatusEnum: any, stepIndex: number) {
        this.hasStateChanged = true;

        for (let i = 0; i < stepIndex; i++) {
            if (this.model.stepsStatus[i] == ManualTestStepStatus.NOT_EXECUTED ) {
                this.model.stepsStatus[i] = ManualTestStepStatus.PASSED;
            }
        }

        if (stepStatusEnum == ManualTestStepStatus.FAILED) {
            this.model.stepsStatus[stepIndex] = ManualTestStatus.FAILED;
        }

        let allTestsArePassed = true;
        for (const stepStatus of this.model.stepsStatus) {
            if(stepStatus != ManualTestStepStatus.PASSED) {
                allTestsArePassed = false;
                break;
            }
        }
        if (allTestsArePassed) {
            this.model.status = ManualTestStatus.PASSED;
        }
    }

    onTestChange() {
        this.hasStateChanged = true;
    }

    isTestSuiteFinalized(): boolean {
        return this.model.isTestPlanFinalized;
    }
    getTestPathDirectoryAsString(): string {
        return this.path ? this.path.toDirectoryString() : ""
    }

    resetChanges(): void {
        this.ngOnInit();
    }

    saveAction(): void {
        this.manualExecPlansService
            .updateTestRun(this.model)
            .subscribe((manualTest: ManualTest) => {
                this.model = manualTest;
                this.ngOnInit()
                //TODO: refresh tree
            });
    }

    private showNextUnexecutedTest(): void {
        this.manualExecPlansService
            .getPathOfUnExecutedTest(this.path)
            .subscribe((nextPath: Path) => {
                this.urlService.navigateToManualExecPlanRunner(nextPath)
            });
    }
}
