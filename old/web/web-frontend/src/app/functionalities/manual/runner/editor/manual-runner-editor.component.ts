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
import {StepCall} from "../../../../model/step/step-call.model";
import {ManualTestsStatusTreeComponent} from "../../common/manual-tests-status-tree/manual-tests-status-tree.component";
import {AreYouSureModalEnum} from "../../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {AreYouSureModalService} from "../../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {ManualTestPlanOverviewComponent} from "../../plans/overview/item/manual-test-plan-overview.component";

@Component({
    selector: 'manual-runner-editor',
    templateUrl: 'manual-runner-editor.component.html',
    styleUrls: ['manual-runner-editor.component.scss']
})
export class ManualRunnerEditorComponent implements OnInit {

    @Input() tree: ManualTestsStatusTreeComponent;
    @Input() overview: ManualTestPlanOverviewComponent;

    ManualTestStatus = ManualTestStatus;
    StepPhaseEnum = StepPhaseEnum;

    model: ManualTest;
    planPath: Path;
    testPath: Path;
    isEditMode = false;
    isLastUnExecutedTest = true

    testStatusDropdownOptions = [
        {label:'Not Executed', value: ManualTestStatus.NOT_EXECUTED},
        {label:'Passed', value: ManualTestStatus.PASSED},
        {label:'Failed', value: ManualTestStatus.FAILED},
        {label:'Blocked', value: ManualTestStatus.BLOCKED},
        {label:'Not Applicable', value: ManualTestStatus.NOT_APPLICABLE},
    ];

    descriptionMarkdownEditor: MarkdownEditorComponent;
    @ViewChild("descriptionMarkdownEditor") set setDescriptionMarkdownEditor(descriptionMarkdownEditor: MarkdownEditorComponent) {
        if (descriptionMarkdownEditor != null) {
            descriptionMarkdownEditor.setEditMode(this.isEditMode);
            descriptionMarkdownEditor.setValue(this.model.description);
        }
        this.descriptionMarkdownEditor = descriptionMarkdownEditor;
    }
    commentMarkdownEditor: MarkdownEditorComponent;
    @ViewChild("commentMarkdownEditor") set setCommentMarkdownEditor(commentMarkdownEditor: MarkdownEditorComponent) {
        if (commentMarkdownEditor != null) {
            commentMarkdownEditor.setValue(this.model.comments);
            commentMarkdownEditor.setEditMode(this.isEditMode);
        }
        this.commentMarkdownEditor = commentMarkdownEditor;
    }

    steps: Array<StepCall[]> = [];

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private manualExecPlansService: ManualTestPlansService,
                private urlService: UrlService,
                private areYouSureModalService: AreYouSureModalService) {
    }

    ngOnInit(): void {
        this.activatedRoute.params.subscribe(params => {
            this.init(params);
        });
    }

    init(queryParams: Params) {
        this.setEditMode(false);
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
                    this.descriptionMarkdownEditor.setEditMode(this.isEditMode);
                }
                if (this.commentMarkdownEditor) {
                    this.commentMarkdownEditor.setValue(this.model.comments);
                    this.commentMarkdownEditor.setEditMode(this.isEditMode);
                }

                let previewsStepPhase: StepPhaseEnum = null;
                for (const manualStepCall of manualTest.stepCalls) {
                    let stepCallForView = manualStepCall.stepCall.clone();
                    if(previewsStepPhase == stepCallForView.stepDef.phase) {
                        stepCallForView.stepDef.phase = StepPhaseEnum.AND;
                    }
                    previewsStepPhase = manualStepCall.stepCall.stepDef.phase;
                    this.steps.push([stepCallForView])
                }

                this.manualExecPlansService
                .getPathOfUnExecutedTest(this.planPath, manualTest.path.toString())
                .subscribe((nextPath: Path) => {
                    this.isLastUnExecutedTest = nextPath == null
                });
            });
        } else {
            this.model = null;
        }
    }

    setEditMode(editMode: boolean) {
        if (this.isEditMode == editMode) {
            return;
        }

        this.isEditMode = editMode;
        if (this.descriptionMarkdownEditor) {
            this.descriptionMarkdownEditor.setValue(this.model.description);
            this.descriptionMarkdownEditor.setEditMode(editMode);
        }
        if (this.commentMarkdownEditor) {
            this.commentMarkdownEditor.setValue(this.model.comments);
            this.commentMarkdownEditor.setEditMode(editMode);
        }
    }

    stepStatusChanged(stepStatusEnum: any, stepIndex: number) {
        this.setEditMode(true);

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
        this.setEditMode(true);
    }

    isFinalized(): boolean {
        return this.model.finalized;
    }

    getTestPathDirectoryAsString(): string {
        return this.testPath ? this.testPath.toDirectoryString() : ""
    }

    cancelChanges(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Cancel",
            "Are you sure you want to cancel all your changes?"
        ).subscribe((action: AreYouSureModalEnum) => {
            if (action == AreYouSureModalEnum.OK) {
                this.init(this.activatedRoute.snapshot.params);
            }
        });
    }

    shouldDisplayComment(): boolean{
        return this.model.status == ManualTestStatus.FAILED
            || this.model.status == ManualTestStatus.BLOCKED
            || this.model.status == ManualTestStatus.NOT_APPLICABLE
    }

    saveAction(): void {
        if (this.shouldDisplayComment()) {
            this.model.comments = this.commentMarkdownEditor.getValue();
        } else {
            this.model.comments = null;
        }

        this.manualExecPlansService
            .updateTestRun(this.planPath, this.model)
            .subscribe((manualTest: ManualTest) => {
                this.init(this.activatedRoute.snapshot.params);
                this.tree.ngOnInit();
                this.overview.ngOnInit();
            });
    }

    private showNextUnExecutedTest(): void {
        this.manualExecPlansService
            .getPathOfUnExecutedTest(this.planPath, this.testPath.toString())
            .subscribe((nextPath: Path) => {
                this.urlService.navigateToManualExecPlanTestRunner(this.planPath, nextPath)
            });
    }
}
