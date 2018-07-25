import {Component, Input, OnInit} from '@angular/core';
import {ManualTestsRunner} from "../../model/manual-tests-runner.model";
import {Router} from "@angular/router";
import {ManualTestsRunnerStatus} from "../../model/enums/manual-tests-runner-status.enum";

@Component({
    selector: 'manual-tests-overview-runner',
    templateUrl: 'manual-tests-overview-runner.component.html',
    styleUrls: ['manual-tests-overview-runner.component.scss']
})

export class ManualTestsOverviewRunnerComponent implements OnInit {

    @Input() model: ManualTestsRunner;
    @Input() executionMode: boolean = false;

    ManualTestsRunnerStatus = ManualTestsRunnerStatus;

    constructor(private router: Router) {
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

    showEditor() {
        this.router.navigate(["manual/runner/show", {path : this.model.path.toString()} ])
    }

    navigateToExecutorMode() {
        this.router.navigate(["manual/execute", {runnerPath : this.model.path.toString()} ])
    }
}
