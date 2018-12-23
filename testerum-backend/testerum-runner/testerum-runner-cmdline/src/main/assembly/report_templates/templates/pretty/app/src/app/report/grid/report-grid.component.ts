import {Component, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ReportService} from "../report.service";
import {ReportGridNode} from "./model/report-grid-node.model";
import {ReportGridNodeMapper} from "./util/report-grid-node.mapper";
import {DateUtil} from "../../util/date.util";
import {ExecutionStatus} from "../../../../../../../common/testerum-model/model/report/execution-status";
import {ReportGridNodeType} from "./model/enums/report-grid-node-type.enum";
import {LogsModalService} from "./logs-modal/logs-modal.service";
import {ReportGridNodeData} from "./model/report-grid-node-data.model";
import {ReportGridFilter} from "./model/report-grid-filter.model";
import {ArrayUtil} from "../../util/array.util";
import {AutoComplete} from "primeng/autocomplete";

@Component({
    selector: 'report-grid',
    templateUrl: './report-grid.component.html',
    styleUrls: ['./report-grid.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ReportGridComponent implements OnInit {

    suiteGridRootNodes: ReportGridNode[];
    filter: ReportGridFilter = new ReportGridFilter();

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    selectedTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    ExecutionStatus = ExecutionStatus;
    ReportGridNodeType = ReportGridNodeType;

    constructor(private reportService: ReportService,
                private logsModalService: LogsModalService) {
    }

    ngOnInit() {
        this.refreshGrid();
    }

    private refreshGrid() {
        let reportSuite = this.reportService.reportModelExtractor.reportSuite;
        this.suiteGridRootNodes = ReportGridNodeMapper.map(reportSuite, this.filter);
    }

    durationString(durationMillis: number): string {
        return DateUtil.durationToShortString(durationMillis)
    }

    onShowLogs(nodeData: ReportGridNodeData) {
        this.logsModalService.showLogsModal(nodeData.logs, nodeData.exceptionDetail);
    }

    onToggleFolders() {
        this.filter.areTestFoldersShown = !this.filter.areTestFoldersShown;
        this.refreshGrid();
    }

    onTogglePassed() {
        this.filter.showPassed = !this.filter.showPassed;
        this.refreshGrid();
    }

    onToggleFailed() {
        this.filter.showFailed = !this.filter.showFailed;
        this.refreshGrid();
    }

    onToggleDisabled() {
        this.filter.showDisabled = !this.filter.showDisabled;
        this.refreshGrid();
    }

    onToggleUndefined() {
        this.filter.showUndefined = !this.filter.showUndefined;
        this.refreshGrid();
    }

    onToggleSkipped() {
        this.filter.showSkipped = !this.filter.showSkipped;
        this.refreshGrid();
    }

    onExpandToTests() {
        this.expandNodesThatMatchExpression(this.suiteGridRootNodes, (node: ReportGridNode) => {
            return node.data.nodeType == ReportGridNodeType.SUITE ||
                node.data.nodeType == ReportGridNodeType.FEATURE;
        });

        this.suiteGridRootNodes = ArrayUtil.copyArrayOfObjects(this.suiteGridRootNodes);
    }

    onExpandToSteps() {
        this.expandNodesThatMatchExpression(this.suiteGridRootNodes, (node: ReportGridNode) => {
            return node.data.nodeType == ReportGridNodeType.SUITE ||
                node.data.nodeType == ReportGridNodeType.FEATURE ||
                node.data.nodeType == ReportGridNodeType.TEST;
        });

        this.suiteGridRootNodes = ArrayUtil.copyArrayOfObjects(this.suiteGridRootNodes)

    }

    onExpandAllNodes() {
        this.expandNodesThatMatchExpression(this.suiteGridRootNodes, (node: ReportGridNode) => {
            return true;
        });

        this.suiteGridRootNodes = ArrayUtil.copyArrayOfObjects(this.suiteGridRootNodes)
    }

    private expandNodesThatMatchExpression(nodes: ReportGridNode[], expresion: any) {
        for (const node of nodes) {
            node.expanded = expresion(node);
            this.expandNodesThatMatchExpression(node.children as ReportGridNode[], expresion);
        }
    }

    onTagsButtonClickEvent() {
        this.filter.isTagsButtonActive = !this.filter.isTagsButtonActive;
    }

    onTagSelect(event) {
        this.currentTagSearch = null;
    }

    onTagUnSelect(event) {
    }

    searchTags(event) {
        this.currentTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allKnownTags,
            event.query
        );
        for (let currentTag of this.selectedTags) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToShow = newTagsToShow;

    }

}
