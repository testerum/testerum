import {Component, Input} from '@angular/core';
import {TestsRunnerLogsComponent} from "../tests-runner-logs.component";
import {TestsRunnerService} from "../../tests-runner.service";

@Component({
    selector: 'tests-runner-logs-toolbar',
    templateUrl: './tests-runner-logs-toolbar.component.html',
    styleUrls: ['./tests-runner-logs-toolbar.component.scss']
})
export class TestsRunnerLogsToolbarComponent {

    @Input() logsComponent: TestsRunnerLogsComponent;
    shouldWrapLogs: boolean = false;

    constructor(private testsRunnerService: TestsRunnerService) {
    }

    onCloseTestSuite() {
        this.testsRunnerService.isTestRunnerVisible = false;
        this.testsRunnerService.stopExecution();
    }

    onToggleWrap() {
        this.shouldWrapLogs = !this.shouldWrapLogs;
        this.logsComponent.wrapLogs(this.shouldWrapLogs)
    }
}
