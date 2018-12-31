import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TestsRunnerService} from "../../tests-runner.service";
import {LogLevel} from "../../../../../model/test/event/enums/log-level.enum";

@Component({
    selector: 'tests-runner-logs-toolbar',
    templateUrl: './tests-runner-logs-toolbar.component.html',
    styleUrls: ['./tests-runner-logs-toolbar.component.scss']
})
export class TestsRunnerLogsToolbarComponent {

    @Input() reportMode: boolean = false;
    @Input() shouldWrapLogs: boolean = false;
    @Output() shouldWrapLogsChange = new EventEmitter<boolean>();
    @Input() minLogLevelToShow: LogLevel = LogLevel.INFO;
    @Output() minLogLevelToShowChange = new EventEmitter<LogLevel>();

    LogLevel= LogLevel;
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

    onLogLevelChange(logLevel: LogLevel) {
        if (this.minLogLevelToShow == logLevel) {
            logLevel = logLevel+1;
        }
        this.minLogLevelToShow = logLevel;
        this.minLogLevelToShowChange.emit(logLevel);
    }
}
