import {Component, Input,} from '@angular/core';
import {ReportLog} from "../../../../../../../../../common/testerum-model/model/report/report-log";
import {LogLevel} from "../../../../../../../../../common/testerum-model/model/report/log-level";

@Component({
    selector: 'logs',
    templateUrl: 'logs.component.html',
    styleUrls: ["logs.component.scss"]
})
export class LogsComponent {

    @Input() logs:Array<ReportLog> = [];

    @Input() shouldWrapLogs: boolean;

    LogLevel = LogLevel;
}
