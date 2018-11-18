import {Component, Input, OnInit} from '@angular/core';
import {ManualTestPlan} from "../../model/manual-test-plan.model";
import {UrlService} from "../../../../../service/url.service";
import {ManualTestPlanStatus} from "../../model/enums/manual-test-plan-status.enum";

@Component({
    selector: 'manual-test-plan-overview',
    templateUrl: './manual-test-plan-overview.component.html',
    styleUrls: ['./manual-test-plan-overview.component.scss']
})
export class ManualTestPlanOverviewComponent implements OnInit {

    @Input() model: ManualTestPlan;
    @Input() executionMode: boolean = false;

    ManualExecPlanStatus = ManualTestPlanStatus;

    constructor(private urlService: UrlService) {
    }

    ngOnInit() {
    }

    percentageFromTotalTest(amount: number): number {
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
