import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {StepPhaseEnum} from "../../../../model/enums/step-phase.enum";
import {ManualTestStatus} from "../../plans/model/enums/manual-test-status.enum";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {ManualTestPlansService} from "../../service/manual-test-plans.service";
import {ManualTest} from "../../plans/model/manual-test.model";
import {ManualTestStepStatus} from "../../plans/model/enums/manual-test-step-status.enum";
import {UrlService} from "../../../../service/url.service";
import {MarkdownEditorComponent} from "../../../../generic/components/markdown-editor/markdown-editor.component";
import {StepCall} from "../../../../model/step-call.model";
import {ManualTestsStatusTreeComponent} from "../../common/manual-tests-status-tree/manual-tests-status-tree.component";

@Component({
    selector: 'manual-runner-editor',
    templateUrl: 'manual-runner-editor.component.html',
    styleUrls: ['manual-runner-editor.component.scss']
})
export class ManualRunnerEditorComponent implements OnInit {

    @Input() tree: ManualTestsStatusTreeComponent;

    ManualTestStatus = ManualTestStatus;
    StepPhaseEnum = StepPhaseEnum;

    markdownEditorOptions = {
        status: false,
        spellChecker: false
    };

    model: ManualTest;
    planPath: Path;
    testPath: Path;
    hasStateChanged = false;

    testStatusDropdownOptions = [
        {label:'Not Executed', value: ManualTestStatus.NOT_EXECUTED},
        {label:'Passed', value: ManualTestStatus.PASSED},
        {label:'Failed', value: ManualTestStatus.FAILED},
        {label:'Blocked', value: ManualTestStatus.BLOCKED},
        {label:'Not Applicable', value: ManualTestStatus.NOT_APPLICABLE},
    ];
    @ViewChild("descriptionMarkdownEditor") descriptionMarkdownEditor: MarkdownEditorComponent;
    @ViewChild("commentMarkdownEditor") commentMarkdownEditor: MarkdownEditorComponent;

    steps: Array<StepCall[]> = [];

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private manualExecPlansService: ManualTestPlansService,
                private urlService: UrlService) {
    }

    ngOnInit(): void {
        this.activatedRoute.params.subscribe(params => {
            this.init(params);
        });
    }

    init(queryParams: Params) {
        this.hasStateChanged = false;
        this.steps = [];

        let planPathAsString = queryParams["planPath"];
        this.planPath = planPathAsString ? Path.createInstance(planPathAsString) : null;

        let testPathAsString = queryParams["testPath"];
        this.testPath = testPathAsString ? Path.createInstance(testPathAsString) : null;

        if (this.testPath) {
            this.manualExecPlansService.getManualTest(this.planPath, this.testPath).subscribe((manualTest: ManualTest) => {
                this.model = manualTest;
                if (this.descriptionMarkdownEditor) {
                    this.descriptionMarkdownEditor.setValue(this.model.description);
                }
                if (this.commentMarkdownEditor) {
                    this.commentMarkdownEditor.setValue(this.model.comments);
                }

                for (const stepCall of manualTest.stepCalls) {
                    this.steps.push([stepCall.stepCall])
                }
            });
        } else {
            this.model = null;
        }
    }

    stepStatusChanged(stepStatusEnum: any, stepIndex: number) {
        this.hasStateChanged = true;

        for (let i = 0; i < stepIndex; i++) {
            if (this.model.stepCalls[i].status == ManualTestStepStatus.NOT_EXECUTED ) {
                this.model.stepCalls[i].status = ManualTestStepStatus.PASSED;
            }
        }

        if (stepStatusEnum == ManualTestStepStatus.FAILED) {
            this.model.stepCalls[stepIndex].status = ManualTestStepStatus.FAILED;
        }

        this.model.status = this.calculateTestStatus();
    }

    private calculateTestStatus() {
        for (const stepCall of this.model.stepCalls) {
            switch (stepCall.status) {
                case ManualTestStepStatus.NOT_EXECUTED: return ManualTestStatus.NOT_EXECUTED;
                case ManualTestStepStatus.FAILED: return ManualTestStatus.FAILED;
            }
        }

        return ManualTestStatus.PASSED;
    }

    onTestChange() {
        this.hasStateChanged = true;
    }

    isFinalized(): boolean {
        return this.model.finalized;
    }

    getTestPathDirectoryAsString(): string {
        return this.testPath ? this.testPath.toDirectoryString() : ""
    }

    resetChanges(): void {
        this.init(this.activatedRoute.snapshot.params);
    }

    saveAction(): void {
        this.manualExecPlansService
            .updateTestRun(this.planPath, this.model)
            .subscribe((manualTest: ManualTest) => {
                this.init(this.activatedRoute.snapshot.params);
                this.tree.ngOnInit();
            });
    }

    private showNextUnExecutedTest(): void {
        this.manualExecPlansService
            .getPathOfUnExecutedTest(this.planPath, this.testPath)
            .subscribe((nextPath: Path) => {
                this.urlService.navigateToManualExecPlanTestRunner(this.planPath, nextPath)
            });
    }
}
