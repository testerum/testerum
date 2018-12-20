import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ReportService} from "../report.service";
import {ReportGridNode} from "./model/report-grid-node.model";
import {ReportGridNodeMapper} from "./util/report-grid-node.mapper";
import {DateUtil} from "../../util/date.util";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/model/report/execution-status";
import {ReportGridNodeType} from "./model/enums/report-grid-node-type.enum";
import {ReportLog} from "../../../../../../../common/testerum-model/model/report/report-log";

@Component({
    selector: 'report-grid',
    templateUrl: './report-grid.component.html',
    styleUrls: ['./report-grid.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ReportGridComponent implements OnInit {

    suiteGridRootNodes: ReportGridNode[] = [];
    ExecutionStatus = ExecutionStatus;
    ReportGridNodeType = ReportGridNodeType;

    constructor(private reportService: ReportService) {
    }

    ngOnInit() {
        let reportSuite = this.reportService.reportModelExtractor.reportSuite;
        this.suiteGridRootNodes.length = 0;
        for (const node of ReportGridNodeMapper.map(reportSuite)) {
            this.suiteGridRootNodes.push(node)
        }
    }

    durationString(durationMillis: number): string {
        return DateUtil.durationToShortString(durationMillis)
    }

    onShowLogs(logs: Array<ReportLog>) {
        
    }
}
