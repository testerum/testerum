import {Component, OnInit} from '@angular/core';
import {ReportService} from "../service/report.service";
import {ExecutionStatus} from "../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {ReportModelExtractor} from "../../../../../../common/testerum-model/report-model/model-extractor/report-model-extractor";
import {ReportUrlService} from "../service/report-url.service";
import {ReportGridNodeMapper} from "../report/grid/util/report-grid-node.mapper";
import {ReportGridFilter} from "../report/grid/model/report-grid-filter.model";
import {ReportGridNode} from "../report/grid/model/report-grid-node.model";
import {ActivatedRoute} from "@angular/router";

@Component({
    selector: 'app-tag-overview',
    templateUrl: './tag-overview.component.html',
    styleUrls: ['./tag-overview.component.scss']
})
export class TagOverviewComponent implements OnInit {

    hideLinksToOtherReports = false;
    statusByTagMap: Map<string, ExecutionStatus>;

    ExecutionStatus = ExecutionStatus;

    constructor(private activatedRoute: ActivatedRoute,
                private reportService: ReportService,
                private reportUrlService: ReportUrlService) {
    }

    ngOnInit() {
        let reportModelExtractor = this.reportService.reportModelExtractor;
        this.statusByTagMap = this.extractStatusByTagMap(reportModelExtractor);

        this.hideLinksToOtherReports = (this.activatedRoute.snapshot.queryParams["hideLinksToOtherReports"] == "true");
    }

    private extractStatusByTagMap(reportModelExtractor: ReportModelExtractor): Map<string, ExecutionStatus> {
        let result = new Map<string, ExecutionStatus>();

        let reportGridNodes: ReportGridNode[] = ReportGridNodeMapper.map(this.reportService.reportModelExtractor.reportSuite, new ReportGridFilter());


        const reportStepsByTag: Map<string, ExecutionStatus> = this.getExecutionStatusMapByTag(reportGridNodes);
        let tags = Array.from(reportStepsByTag.keys());
        tags.sort();

        for (const tag of tags) {
            result.set(tag, reportStepsByTag.get(tag));
        }

        return result;
    }

    onTagClick(tag: string) {
        this.reportUrlService.navigateToTagReport(tag);
    }

    goToFullReport() {
        this.reportUrlService.navigateReport();
    }

    getExecutionStatusMapByTag(reportGridNodes: ReportGridNode[]): Map<string, ExecutionStatus> {
        let result = new Map<string, ExecutionStatus>();

        for (const node of reportGridNodes) {
            this.addNodeTagsToTagExecutionStatusMap(node, result);
        }

        return result;
    }


    private addNodeTagsToTagExecutionStatusMap(node: ReportGridNode, result: Map<string, ExecutionStatus>) {
        for (const tag of node.data.tags) {

            let oldStatus = result.get(tag);
            let newStatus = node.data.status;
            if (oldStatus && newStatus < oldStatus) {
                newStatus = oldStatus;
            }
            result.set(tag, newStatus);
        }

        for (const child of node.children) {
            this.addNodeTagsToTagExecutionStatusMap(child as ReportGridNode, result);
        }
    }
}
