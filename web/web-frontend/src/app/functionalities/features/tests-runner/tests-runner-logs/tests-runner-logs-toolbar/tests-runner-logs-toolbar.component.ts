import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TestsRunnerService} from "../../tests-runner.service";

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
