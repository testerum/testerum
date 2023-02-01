import {Component, Input, OnInit} from '@angular/core';
import {ReportService} from "../../service/report.service";
import {DateUtil} from "../../util/date.util";

@Component({
    selector: 'report-title',
    templateUrl: './title.component.html',
    styleUrls: ['./title.component.scss']
})
export class TitleComponent implements OnInit {

    @Input() titlePrefix: string;
    suiteName: string;
    executionDate: string;
    duration: string;

    constructor(private reportService: ReportService) {
    }

    ngOnInit() {
        let reportSuite = this.reportService.reportModelExtractor.reportSuite;

        this.suiteName = this.titlePrefix + (reportSuite.executionName ? (" - " + reportSuite.executionName) : "");
        this.executionDate = DateUtil.dateTimeToShortString(reportSuite.startTime);
        this.duration = DateUtil.durationToLongString(reportSuite.durationMillis);
    }

}
