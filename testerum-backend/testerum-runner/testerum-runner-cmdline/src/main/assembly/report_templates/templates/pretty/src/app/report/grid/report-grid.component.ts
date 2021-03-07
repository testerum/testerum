import {Component, OnDestroy, OnInit, ViewChild, ViewEncapsulation} from '@angular/core';
import {ReportService} from "../../service/report.service";
import {ReportGridNode} from "./model/report-grid-node.model";
import {ReportGridNodeMapper} from "./util/report-grid-node.mapper";
import {DateUtil} from "../../util/date.util";
import {ExecutionStatus} from "../../../../../../common/testerum-model/report-model/model/report/execution-status";
import {ReportGridNodeType} from "./model/enums/report-grid-node-type.enum";
import {LogsModalService} from "./logs-modal/logs-modal.service";
import {ReportGridNodeData} from "./model/report-grid-node-data.model";
import {ReportGridFilter} from "./model/report-grid-filter.model";
import {ArrayUtil} from "../../util/array.util";
import {AutoComplete} from "primeng/autocomplete";
import {ReportGridTagsUtil} from "./util/report-grid-tags.util";
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";
import {ReportLog} from "../../../../../../common/testerum-model/report-model/model/report/report-log";

@Component({
    selector: 'report-grid',
    templateUrl: './report-grid.component.html',
    styleUrls: ['./report-grid.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class ReportGridComponent implements OnInit, OnDestroy {

    suiteGridRootNodes: ReportGridNode[];
    filter: ReportGridFilter = new ReportGridFilter();

    @ViewChild("tagsElement") tagsAutoComplete: AutoComplete;
    allKnownTags: Array<string> = [];
    tagsToShow:string[] = [];
    currentTagSearch:string;

    ExecutionStatus = ExecutionStatus;
    ReportGridNodeType = ReportGridNodeType;

    private routeParamsSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                private reportService: ReportService,
                private logsModalService: LogsModalService) {
    }

    ngOnInit() {
        this.routeParamsSubscription = this.route.params.subscribe(params => {
            if (params.tag) {
                this.filter.isTagsButtonActive = true;
                this.filter.selectedTags.push(
                    params.tag
                );
            }
        });
        this.refreshGrid();
    }

    ngOnDestroy(): void {
        if(this.routeParamsSubscription) {this.routeParamsSubscription.unsubscribe()}
    }

    private refreshGrid() {
        let reportSuite = this.reportService.reportModelExtractor.reportSuite;
        this.suiteGridRootNodes = ReportGridNodeMapper.map(reportSuite, this.filter);
        this.allKnownTags = ReportGridTagsUtil.getTags(this.suiteGridRootNodes);
    }

    durationString(durationMillis: number): string {
        return DateUtil.durationToShortString(durationMillis)
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
                   node.data.nodeType == ReportGridNodeType.FEATURE ||
                   node.data.nodeType == ReportGridNodeType.PARAMETRIZED_TEST
        });

        this.suiteGridRootNodes = ArrayUtil.copyArrayOfObjects(this.suiteGridRootNodes);
    }

    onExpandToSteps() {
        this.expandNodesThatMatchExpression(this.suiteGridRootNodes, (node: ReportGridNode) => {
            return node.data.nodeType == ReportGridNodeType.SUITE ||
                node.data.nodeType == ReportGridNodeType.FEATURE ||
                node.data.nodeType == ReportGridNodeType.TEST ||
                node.data.nodeType == ReportGridNodeType.PARAMETRIZED_TEST ||
                node.data.nodeType == ReportGridNodeType.SCENARIO
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
        if (!this.filter.isTagsButtonActive) {
            this.filter.selectedTags.length = 0;
            this.refreshGrid();
        }
    }

    onTagSelect(event) {
        this.currentTagSearch = null;
        this.filter.selectedTags.push(event);
        this.refreshGrid();
    }

    onTagUnSelect(event) {
        ArrayUtil.removeElementFromArray(this.filter.selectedTags, event);
        this.refreshGrid();
    }

    searchTags(event) {
        this.currentTagSearch = event.query;

        let newTagsToShow = ArrayUtil.filterArray(
            this.allKnownTags,
            event.query
        );
        for (let currentTag of this.filter.selectedTags) {
            ArrayUtil.removeElementFromArray(newTagsToShow, currentTag)
        }
        this.tagsToShow = newTagsToShow;
    }

    onDownloadLogs(nodeData: ReportGridNodeData) {
        var element = document.createElement('a');
        element.setAttribute('href', nodeData.textLogFilePath);
        element.setAttribute('download', "logs.txt");
        element.setAttribute('target', "_blank");

        element.style.display = 'none';
        document.body.appendChild(element);

        element.click();

        document.body.removeChild(element);
    }

    onShowLogs(nodeData: ReportGridNodeData) {
        window['receiveModel'] = this.onLogsLoad;
        window['logsModalService'] = this.logsModalService;

        var element = document.createElement('script');
        element.setAttribute('type', 'text/javascript');
        element.setAttribute('src', nodeData.modelLogFilePath);
        document.body.appendChild(element);

    }

    onLogsLoad(data: ReportLog[]) {
        window['logsModalService'].showLogsModal(data);
    }

    getGridLineTitle(nodeData: ReportGridNodeData): string {
        let result = "";
        switch (nodeData.nodeType) {
            case ReportGridNodeType.SUITE: {result += "Test Suite"; break;}
            case ReportGridNodeType.FEATURE: {result += "Feature"; break;}
            case ReportGridNodeType.TEST: {result += "Test"; break;}
            case ReportGridNodeType.PARAMETRIZED_TEST: {result += "Parametrized Test"; break;}
            case ReportGridNodeType.SCENARIO: {result += "Scenario"; break;}
            case ReportGridNodeType.COMPOSED_STEP: {result += "Composed Step"; break;}
            case ReportGridNodeType.BASIC_STEP: {result += "Basic Step"; break;}
            case ReportGridNodeType.UNDEFINED_STEP: {result += "Undefined Step"; break;}
        }

        switch (nodeData.status) {
            case ExecutionStatus.PASSED: result += " has Passed"; break;
            case ExecutionStatus.FAILED: result += " has Failed"; break;
            case ExecutionStatus.DISABLED: result += " is Disabled"; break;
            case ExecutionStatus.UNDEFINED: result += " is Undefined Steps"; break;
            case ExecutionStatus.SKIPPED: result += " was Skipped"; break;
        }

        return result;
    }
}
