import {Component, OnInit} from '@angular/core';
import {ReportService} from "../../service/report.service";
import {DateUtil} from "../../util/date.util";

@Component({
    selector: 'report-title',
    templateUrl: './title.component.html',
    styleUrls: ['./title.component.scss']
})
export class TitleComponent implements OnInit {

    suiteName: string;
    executionDate: string;
    duration: string;

    constructor(private reportService: ReportService) {
    }

    ngOnInit() {
        let reportSuite = this.reportService.reportModelExtractor.reportSuite;

        this.suiteName = reportSuite.name;
        this.executionDate = DateUtil.dateTimeToShortString(reportSuite.startTime);
        this.duration = DateUtil.durationToShortString(reportSuite.durationMillis);
    }

}
