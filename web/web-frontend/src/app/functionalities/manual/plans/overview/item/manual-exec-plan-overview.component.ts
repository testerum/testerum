import {Component, Input, OnInit} from '@angular/core';
import {ManualExecPlan} from "../../model/manual-exec-plan.model";
import {UrlService} from "../../../../../service/url.service";
import {ManualExecPlanStatus} from "../../model/enums/manual-exec-plan-status.enum";

@Component({
    selector: 'manual-exec-plan-overview',
    templateUrl: './manual-exec-plan-overview.component.html',
    styleUrls: ['./manual-exec-plan-overview.component.scss']
})
export class ManualExecPlanOverviewComponent implements OnInit {

    @Input() model: ManualExecPlan;
    @Input() executionMode: boolean = false;

    ManualExecPlanStatus = ManualExecPlanStatus;

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
        ${this.model.notExecutedTests} Not Executed`
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
