import {Component, OnInit} from '@angular/core';
import {ReportService} from "../service/report.service";
import {ExecutionStatus} from "../../../../../../common/testerum-model/model/report/execution-status";
import {ReportModelExtractor} from "../../../../../../common/testerum-model/model-extractor/report-model-extractor";
import {ReportStep} from "../../../../../../common/testerum-model/model/report/report-step";
import {ReportUrlService} from "../service/report-url.service";

@Component({
    selector: 'app-tag-overview',
    templateUrl: './tag-overview.component.html',
    styleUrls: ['./tag-overview.component.scss']
})
export class TagOverviewComponent implements OnInit {

    statusByTagMap: Map<string, ExecutionStatus>;

    ExecutionStatus = ExecutionStatus;

    constructor(private reportService: ReportService,
                private reportUrlService: ReportUrlService) {
    }

    ngOnInit() {
        let reportModelExtractor = this.reportService.reportModelExtractor;
        this.statusByTagMap = this.extractStatusByTagMap(reportModelExtractor);
    }

    private extractStatusByTagMap(reportModelExtractor: ReportModelExtractor): Map<string, ExecutionStatus> {
        let result = new Map<string, ExecutionStatus>();

        const reportStepsByTag: Map<string, Array<ReportStep>> = reportModelExtractor.getReportStepsMapByTag();
        let tags = Array.from(reportStepsByTag.keys());
        tags.sort();

        for (const tag of tags) {
            let status: ExecutionStatus = this.extractStatusFromReportSteps(reportStepsByTag.get(tag));

            result.set(tag, status);
        }

        return result;
    }

    private extractStatusFromReportSteps(reportSteps: Array<ReportStep>): ExecutionStatus {
        let result: ExecutionStatus = ExecutionStatus.SKIPPED;

        for (const reportStep of reportSteps) {
            if (result < reportStep.status) {
                result = reportStep.status;
            }
        }

        return result;
    }

    onTagClick(tag: string) {
        this.reportUrlService.navigateToTagReport(tag);
    }
}
