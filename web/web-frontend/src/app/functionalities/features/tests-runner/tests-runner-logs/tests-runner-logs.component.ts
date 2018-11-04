import {AfterViewChecked, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild,} from '@angular/core';
import {TestsRunnerLogModel} from "./model/tests-runner-log.model";
import {LogLineTypeEnum} from "./model/log-line-type.enum";
import {RunnerTreeNodeModel} from "../tests-runner-tree/model/runner-tree-node.model";
import {TestsRunnerLogsService} from "./tests-runner-logs.service";
import {Subscription} from "rxjs";
import {TestsRunnerService} from "../tests-runner.service";
import {RunnerRootTreeNodeModel} from "../tests-runner-tree/model/runner-root-tree-node.model";

@Component({
    moduleId: module.id,
    selector: 'tests-runner-logs',
    templateUrl: 'tests-runner-logs.component.html',
    styleUrls: ["tests-runner-logs.component.scss"]
})
export class TestsRunnerLogsComponent implements AfterViewChecked, OnInit, OnDestroy {

    @Input() logs:Array<TestsRunnerLogModel> = [];
    @Input() showLastLogWhenChanged = true;
    @Input() reportMode: boolean = false;

    @ViewChild('logsFooter') scrollContainer: ElementRef;

    logsToDisplay: Array<TestsRunnerLogModel> = [];
    selectedRunnerTreeNode: RunnerTreeNodeModel;
    shouldWrapLogs: boolean;

    private lastLogsCount: number = 0;

    LogLineTypeEnum = LogLineTypeEnum;

    logAddedEventEmitterSubscription:any;
    emptyLogsEventEmitterSubscription:any;
    selectedRunnerTreeNodeSubscription: Subscription;

    constructor(private testRunnerService: TestsRunnerService,
                private testsRunnerLogsService: TestsRunnerLogsService) {
    }

    ngOnInit(): void {
        this.lastLogsCount = this.logs.length;

        this.logsToDisplay.length = 0;
        for (let log of this.logs) {
            this.logsToDisplay.push(log);
        }

        this.selectedRunnerTreeNodeSubscription = this.testRunnerService.selectedRunnerTreeNodeObserver.subscribe((selectedNode: RunnerTreeNodeModel) => {
            this.onRunnerTreeNodeSelected(selectedNode);
        });

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
                this.selectedRunnerTreeNode = null;
            }
        );
    }

    ngOnDestroy(): void {
        if (this.selectedRunnerTreeNodeSubscription) {
            this.selectedRunnerTreeNodeSubscription.unsubscribe();
        }
        if (this.logAddedEventEmitterSubscription) {
            this.logAddedEventEmitterSubscription.unsubscribe();
        }
        if (this.emptyLogsEventEmitterSubscription) {
            this.emptyLogsEventEmitterSubscription.unsubscribe();
        }
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

    private onRunnerTreeNodeSelected(runnerTreeNode: RunnerTreeNodeModel): void {
        this.selectedRunnerTreeNode = runnerTreeNode;

        this.logsToDisplay.length = 0;

        for (let log of this.logs) {
            if (runnerTreeNode == null || this.isLogBelogingToRunnerTreeNode(log, runnerTreeNode)) {
                this.logsToDisplay.push(log)
            }
        }
    }

    private isLogBelogingToRunnerTreeNode(log: TestsRunnerLogModel, runnerTreeNode: RunnerTreeNodeModel): boolean {
        if (runnerTreeNode instanceof RunnerRootTreeNodeModel) {
            return true;
        }

        if(!runnerTreeNode.eventKey || !log.eventKey) {
            return false;
        }
        return runnerTreeNode.eventKey.isParentOf(log.eventKey)
    }
}
