import {AfterViewChecked, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild,} from '@angular/core';
import {TestsRunnerLogModel} from "./model/tests-runner-log.model";
import {LogLineTypeEnum} from "./model/log-line-type.enum";
import {RunnerTreeNodeModel} from "../tests-runner-tree/model/runner-tree-node.model";
import {RunnerTreeNodeSelectedListener} from "../tests-runner-tree/event/runner-tree-node-selected.listener";
import {RunnerTreeService} from "../tests-runner-tree/runner-tree.service";
import {TestsRunnerLogsService} from "./tests-runner-logs.service";

@Component({
    moduleId: module.id,
    selector: 'tests-runner-logs',
    templateUrl: 'tests-runner-logs.component.html',
    styleUrls: ["tests-runner-logs.component.scss"]
})

export class TestsRunnerLogsComponent implements AfterViewChecked, OnInit, OnDestroy, RunnerTreeNodeSelectedListener {

    @Input() logs:Array<TestsRunnerLogModel> = [];
    @Input() showLastLogWhenChanged = true;
    @ViewChild('logsFooter') scrollContainer: ElementRef;

    logsToDisplay: Array<TestsRunnerLogModel> = [];
    selectedRunnerTreeNode: RunnerTreeNodeModel;

    private lastLogsCount: number = 0;

    LogLineTypeEnum = LogLineTypeEnum;

    logAddedEventEmitterSubscription:any;
    emptyLogsEventEmitterSubscription:any;

    constructor(private runnerTreeService: RunnerTreeService,
                private testsRunnerLogsService: TestsRunnerLogsService) {
    }

    ngOnInit(): void {
        this.lastLogsCount = this.logs.length;

        this.logsToDisplay.length = 0;
        for (let log of this.logs) {
            this.logsToDisplay.push(log);
        }

        this.runnerTreeService.addSelectedRunnerTreeNodeListeners(this);

        this.logAddedEventEmitterSubscription = this.testsRunnerLogsService.logAddedEventEmitter.subscribe(
            (logModel:TestsRunnerLogModel) => {
                if(this.selectedRunnerTreeNode == null || this.isLogBelogingToRunnerTreeNode(logModel, this.selectedRunnerTreeNode)) {
                    this.logsToDisplay.push(logModel);
                }
            }
        );
        this.emptyLogsEventEmitterSubscription = this.testsRunnerLogsService.emptyLogsEventEmitter.subscribe(
            event => {
                this.logsToDisplay.length = 0;
            }
        );
    }

    ngOnDestroy(): void {
        this.runnerTreeService.removeSelectedRunnerTreeNodeListeners(this);
        this.logAddedEventEmitterSubscription.unsubscribe();
        this.emptyLogsEventEmitterSubscription.unsubscribe();
    }

    updateLogsToDisplayFromLogs() {
        this.logsToDisplay.length = 0;
        for (let log of this.logs) {
            this.logsToDisplay.push(log)
        }
    }

    ngAfterViewChecked(): void {
        if (this.lastLogsCount != this.logs.length && this.showLastLogWhenChanged) {
            this.lastLogsCount = this.logs.length;
            this.scrollContainer.nativeElement.scrollIntoView();
        }
    }

    onRunnerTreeNodeSelected(runnerTreeNode: RunnerTreeNodeModel): void {
        this.selectedRunnerTreeNode = runnerTreeNode;

        this.logsToDisplay.length = 0;

        for (let log of this.logs) {
            if (runnerTreeNode == null || this.isLogBelogingToRunnerTreeNode(log, runnerTreeNode)) {
                this.logsToDisplay.push(log)
            }
        }
    }

    private isLogBelogingToRunnerTreeNode(log: TestsRunnerLogModel, runnerTreeNode: RunnerTreeNodeModel): boolean {
        if(!runnerTreeNode.eventKey) {
            return false;
        }
        return runnerTreeNode.eventKey.isParentOf(log.eventKey)
    }

    toggleWrap() {
        let existingClassNames = document.querySelector(".logs-split-area").className;
        let linesWrapped = (existingClassNames.indexOf("horizontal-wrap") != -1);

        let classNames: string;
        if (linesWrapped) {
            classNames = "logs-split-area horizontal-scroll";
        } else {
            classNames = "logs-split-area horizontal-wrap";
        }

        document.querySelector(".logs-split-area").className = classNames;
    }
}
