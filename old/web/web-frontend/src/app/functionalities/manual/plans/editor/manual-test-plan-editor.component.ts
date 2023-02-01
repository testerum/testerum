import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ManualTestPlan} from "../model/manual-test-plan.model";
import {ManualTestPlansService} from "../../service/manual-test-plans.service";
import {UrlService} from "../../../../service/url.service";
import {MarkdownEditorComponent} from "../../../../generic/components/markdown-editor/markdown-editor.component";
import {AreYouSureModalService} from "../../../../generic/components/are_you_sure_modal/are-you-sure-modal.service";
import {AreYouSureModalEnum} from "../../../../generic/components/are_you_sure_modal/are-you-sure-modal.enum";
import {AbstractComponentCanDeactivate} from "../../../../generic/interfaces/can-deactivate/AbstractComponentCanDeactivate";
import {ManualSelectTestsTreeComponent} from "./manual-select-tests-tree/manual-select-tests-tree.component";
import {ManualTestPlansOverviewService} from "../overview/manual-test-plans-overview.service";
import {Path} from "../../../../model/infrastructure/path/path.model";
import {Message} from "primeng/api";

@Component({
    selector: 'manual-test-plan-editor',
    templateUrl: './manual-test-plan-editor.component.html',
    styleUrls: ['./manual-test-plan-editor.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ManualTestPlanEditorComponent extends AbstractComponentCanDeactivate implements OnInit {

    model: ManualTestPlan = new ManualTestPlan();

    isEditMode: boolean = false;
    isFinalized: boolean = false;
    isCreateAction: boolean = false;

    warnings: Message[] = [];

    pieChartData: any;

    @ViewChild(MarkdownEditorComponent, { static: true }) descriptionMarkdownEditor: MarkdownEditorComponent;
    @ViewChild(ManualSelectTestsTreeComponent) manualSelectTestsTreeComponent: ManualSelectTestsTreeComponent;

    constructor(private route: ActivatedRoute,
                private urlService: UrlService,
                private manualTestPlansOverviewService: ManualTestPlansOverviewService,
                private manualExecPlansService: ManualTestPlansService,
                private areYouSureModalService: AreYouSureModalService) {
        super();
    }

    ngOnInit(): void {

        this.route.data.subscribe(data => {
            let manualTestsRunner = data['manualExecPlan'];

            this.pieChartData = {
                labels: ['Passed Tests', 'Failed Tests', 'Blocked Tests', "Not Applicable Tests", "Not Executed"],
                datasets: [{
                    data: [
                        manualTestsRunner.passedTests,
                        manualTestsRunner.failedTests,
                        manualTestsRunner.blockedTests,
                        manualTestsRunner.notApplicableTests,
                        manualTestsRunner.notExecutedOrInProgressTests
                    ],
                    backgroundColor: ["#5cb85c", "#E6302C", "#FFCE56", "#36A2EB", "#c0bebc"],
                    hoverBackgroundColor: ["#5cb85c", "#E6302C", "#FFCE56", "#36A2EB", "#c0bebc"]
                }]
            };

            this.initialize(manualTestsRunner);
        });
    }

    private initialize(manualTestsRunner: ManualTestPlan) {
        this.model = manualTestsRunner;
        this.warnings = [];

        this.setEditMode(this.model.path.isEmpty());

        if (this.descriptionMarkdownEditor) {
            this.descriptionMarkdownEditor.setValue(manualTestsRunner.description);
        }

        this.isFinalized = manualTestsRunner.isFinalized;

        this.isCreateAction = this.model.path.isEmpty();

        this.pieChartData.datasets[0].data[0] = this.model.passedTests;
        this.pieChartData.datasets[0].data[1] = this.model.failedTests;
        this.pieChartData.datasets[0].data[2] = this.model.blockedTests;
        this.pieChartData.datasets[0].data[3] = this.model.notApplicableTests;
        this.pieChartData.datasets[0].data[4] = this.model.notExecutedOrInProgressTests
    }

    canDeactivate(): boolean {
        return !this.isEditMode;
    }

    setEditMode(editMode: boolean) {
        this.isEditMode = editMode;
        if (this.descriptionMarkdownEditor) {
            this.descriptionMarkdownEditor.setEditMode(editMode);
            this.descriptionMarkdownEditor.setValue(this.model.description);
        }
    }

    enableEditTestMode(): void {
        this.setEditMode(true);
    }

    navigateToExecutorMode() {
        this.manualExecPlansService
            .getPathOfUnExecutedTest(this.model.path, null)
            .subscribe((nextPath: Path) => {
                if (nextPath == null) {
                    this.urlService.navigateToManualExecPlanRunner(this.model.path);

                } else {
                    this.urlService.navigateToManualExecPlanTestRunner(this.model.path, nextPath);
                }
            });
    }

    cancelAction(): void {
        if (this.isCreateAction) {
            this.setEditMode(false); //this is required for CanDeactivate
            this.urlService.navigateToManualExecPlans();
        } else {
            this.manualExecPlansService.getManualExecPlan(this.model.path).subscribe(
                (result: ManualTestPlan) => {
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
            .subscribe((event: AreYouSureModalEnum) => {
                if (event == AreYouSureModalEnum.OK) {
                    this.manualExecPlansService.deleteManualExecPlan(this.model.path).subscribe(result => {
                        this.isEditMode = false; // to not show UnsavedChangesGuard
                        this.manualTestPlansOverviewService.initializeManualPlansOverview();
                        this.urlService.navigateToManualExecPlans();
                    });
                }
            });
    }

    finalize(): void {
        this.areYouSureModalService.showAreYouSureModal(
            "Finalize Execution",
            "Are you sure you want to finalize this Tests Execution?")
            .subscribe((event: AreYouSureModalEnum) => {
                if (event == AreYouSureModalEnum.OK) {
                    this.manualExecPlansService
                        .finalizeManualExecPlan(this.model.path)
                        .subscribe((savedModel: ManualTestPlan) => this.afterSaveHandler(savedModel));
                }
            }
        );
    }

    activate(): void {
        this.manualExecPlansService
            .activatePlan(this.model.path)
            .subscribe((savedModel: ManualTestPlan) => this.afterSaveHandler(savedModel));
    }

    saveAction(): void {

        this.model.manualTreeTests = this.manualSelectTestsTreeComponent.getSelectedTests();
        this.model.description = this.descriptionMarkdownEditor.getValue();

        if (this.model.manualTreeTests.length == 0) {
            this.showWarningThatTestsNeedsToBeSelected();
            return;
        }

        this.manualExecPlansService
            .save(this.model)
            .subscribe((savedModel: ManualTestPlan) => this.afterSaveHandler(savedModel));
    }

    private afterSaveHandler(savedManualTestRunner: ManualTestPlan) {
        this.initialize(savedManualTestRunner);
        this.setEditMode(false);
        this.manualTestPlansOverviewService.initializeManualPlansOverview();
        this.urlService.navigateToManualExecPlanEditor(savedManualTestRunner.path);
    }

    private showWarningThatTestsNeedsToBeSelected() {
        this.warnings = [];
        this.warnings.push(
            {severity: 'error', summary: "Please select the tests that you want to execute in this Test Plan"}
        )
    }
}
