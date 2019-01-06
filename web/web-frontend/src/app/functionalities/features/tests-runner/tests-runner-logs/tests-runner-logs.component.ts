import {AfterViewChecked, Component, ElementRef, Input, OnDestroy, OnInit, ViewChild,} from '@angular/core';
import {TestsRunnerLogModel} from "./model/tests-runner-log.model";
import {LogLineTypeEnum} from "./model/log-line-type.enum";
import {RunnerTreeNodeModel} from "../tests-runner-tree/model/runner-tree-node.model";
import {TestsRunnerLogsService} from "./tests-runner-logs.service";
import {Subscription} from "rxjs";
import {TestsRunnerService} from "../tests-runner.service";
import {RunnerRootTreeNodeModel} from "../tests-runner-tree/model/runner-root-tree-node.model";
import {LogLevel} from "../../../../model/test/event/enums/log-level.enum";
import {StringUtils} from "../../../../utils/string-utils.util";

@Component({
    moduleId: module.id,
    selector: 'tests-runner-logs',
    templateUrl: 'tests-runner-logs.component.html',
    styleUrls: ["tests-runner-logs.component.scss"]
})
export class TestsRunnerLogsComponent implements AfterViewChecked, OnInit, OnDestroy {

    @Input() logsByEventKey:Map<string, Array<TestsRunnerLogModel>> = new Map<string, Array<TestsRunnerLogModel>>();
    @Input() showLastLogWhenChanged = true;
    @Input() reportMode: boolean = false;

    @ViewChild('logsFooter') scrollContainer: ElementRef;

    logsToDisplay: Array<TestsRunnerLogModel> = [];
    selectedRunnerTreeNode: RunnerTreeNodeModel;
    shouldWrapLogs: boolean;
    minLogLevelToShow: LogLevel = LogLevel.INFO;

    private lastLogsCount: number = 0;

    LogLineTypeEnum = LogLineTypeEnum;
    LogLevel = LogLevel;

    logAddedEventEmitterSubscription:any;
    emptyLogsEventEmitterSubscription:any;
    selectedRunnerTreeNodeSubscription: Subscription;

    constructor(private testRunnerService: TestsRunnerService,
                private testsRunnerLogsService: TestsRunnerLogsService) {
    }

    ngOnInit(): void {
        this.lastLogsCount = this.logsByEventKey.size;

        this.logsToDisplay.length = 0;
        this.logsByEventKey.forEach((logs: Array<TestsRunnerLogModel>, key: string) => {
            for (const log of logs) {
                this.logsToDisplay.push(log);
            }
        });

        this.selectedRunnerTreeNodeSubscription = this.testRunnerService.selectedRunnerTreeNodeObserver.subscribe((selectedNode: RunnerTreeNodeModel) => {
            this.onRunnerTreeNodeSelected(selectedNode);
        });

        this.logAddedEventEmitterSubscription = this.testsRunnerLogsService.logAddedEventEmitter.subscribe(
            (logModel:TestsRunnerLogModel) => {
                if(this.selectedRunnerTreeNode == null || this.isLogBelogingToRunnerTreeNode(logModel.eventKey, this.selectedRunnerTreeNode)) {
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

    ngAfterViewChecked(): void {
        if (this.lastLogsCount != this.logsByEventKey.size && this.showLastLogWhenChanged) {
            this.lastLogsCount = this.logsByEventKey.size;
            this.scrollContainer.nativeElement.scrollIntoView();
        }
    }

    private onRunnerTreeNodeSelected(runnerTreeNode: RunnerTreeNodeModel): void {
        this.selectedRunnerTreeNode = runnerTreeNode;
        this.refreshLogs();
    }

    private refreshLogs() {
        this.logsToDisplay.length = 0;
        this.logsByEventKey.forEach((logs: Array<TestsRunnerLogModel>, key: string) => {
            if (this.selectedRunnerTreeNode && !this.isLogBelogingToRunnerTreeNode(key, this.selectedRunnerTreeNode)) {
                return;
            }
            for (const log of logs) {
                if (log.logLevel < this.minLogLevelToShow) {
                    continue;
                }
                this.logsToDisplay.push(log);
            }
        });
    }

    private isLogBelogingToRunnerTreeNode(logEventKeyAsString: string, runnerTreeNode: RunnerTreeNodeModel): boolean {
        if (runnerTreeNode instanceof RunnerRootTreeNodeModel) {
            return true;
        }

        if(!runnerTreeNode.eventKey || !logEventKeyAsString) {
            return false;
        }
        return logEventKeyAsString.startsWith(runnerTreeNode.eventKey.eventKeyAsString)
    }

    onLogLevelChange(event: LogLevel) {
        this.minLogLevelToShow = event;
        this.refreshLogs()
    }
}
