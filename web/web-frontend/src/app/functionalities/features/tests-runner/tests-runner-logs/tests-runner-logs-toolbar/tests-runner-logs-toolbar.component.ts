import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TestsRunnerLogsComponent} from "../tests-runner-logs.component";
import {TestsRunnerService} from "../../tests-runner.service";
import {ManualTestStepStatus} from "../../../../../manual-tests/model/enums/manual-test-step-status.enum";

@Component({
    selector: 'tests-runner-logs-toolbar',
    templateUrl: './tests-runner-logs-toolbar.component.html',
    styleUrls: ['./tests-runner-logs-toolbar.component.scss']
})
export class TestsRunnerLogsToolbarComponent {

    @Input() reportMode: boolean = false;
    @Input() shouldWrapLogs: boolean = false;
    @Output() shouldWrapLogsChange = new EventEmitter<boolean>();

    constructor(private testsRunnerService: TestsRunnerService) {
    }

    onCloseTestSuite() {
        this.testsRunnerService.isTestRunnerVisible = false;
        this.testsRunnerService.stopExecution();
    }

    onToggleWrap() {
        this.shouldWrapLogs = !this.shouldWrapLogs;
        this.shouldWrapLogsChange.emit(this.shouldWrapLogs);
    }
}
