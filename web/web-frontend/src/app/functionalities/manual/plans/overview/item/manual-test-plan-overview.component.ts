import {Component, Input, OnInit} from '@angular/core';
import {ManualTestPlan} from "../../model/manual-test-plan.model";
import {UrlService} from "../../../../../service/url.service";
import {Path} from "../../../../../model/infrastructure/path/path.model";
import {ManualTestPlansService} from "../../../service/manual-test-plans.service";

@Component({
    selector: 'manual-test-plan-overview',
    templateUrl: './manual-test-plan-overview.component.html',
    styleUrls: ['./manual-test-plan-overview.component.scss']
})
export class ManualTestPlanOverviewComponent implements OnInit {

    @Input() model: ManualTestPlan;
    @Input() planPath: Path;
    @Input() executionMode: boolean = false;

    constructor(private urlService: UrlService,
                private manualExecPlansService: ManualTestPlansService) {
    }

    ngOnInit() {
        if (this.planPath) {
            this.manualExecPlansService.getManualExecPlan(this.planPath).subscribe((manualExecPlan: ManualTestPlan) => {
                this.model = manualExecPlan;
            });
        }
    }

    percentageFromTotalTest(amount: number): number {
        if(this.model.totalTests == 0 ) return 0;
        return (amount * 100)/this.model.totalTests;
    }

    progressTooltip(): string {
        return `${this.model.totalTests} Total Tests
        ${this.model.passedTests} Passed
        ${this.model.failedTests} Failed
        ${this.model.blockedTests} Blocked
        ${this.model.notApplicableTests} Not Applicable
        ${this.model.notExecutedOrInProgressTests} Not Executed`
    }

    intNumber(amount: number): number {
        return Math.round(amount);
    }

    navigateToManualExecPlanEditor() {
        this.urlService.navigateToManualExecPlanEditor(this.model.path)
    }

    navigateToManualExecPlanRunner() {
        this.urlService.navigateToManualExecPlanRunner(this.model.path);
    }
}
