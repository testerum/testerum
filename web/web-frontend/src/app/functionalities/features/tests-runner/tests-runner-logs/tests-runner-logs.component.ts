import {
    AfterViewChecked, AfterViewInit,
    ChangeDetectionStrategy, ChangeDetectorRef,
    Component,
    ElementRef,
    OnDestroy,
    OnInit,
    ViewChild,
} from '@angular/core';
import {TestsRunnerLogModel} from "./model/tests-runner-log.model";
import {LogLineTypeEnum} from "./model/log-line-type.enum";
import {RunnerTreeNodeModel} from "../tests-runner-tree/model/runner-tree-node.model";
import {TestsRunnerLogsService} from "./tests-runner-logs.service";
import {interval, merge, Subscription} from "rxjs";
import {TestsRunnerService} from "../tests-runner.service";
import {RunnerRootTreeNodeModel} from "../tests-runner-tree/model/runner-root-tree-node.model";
import {LogLevel} from "../../../../model/test/event/enums/log-level.enum";
import {buffer, bufferTime, map} from "rxjs/operators";

@Component({
    moduleId: module.id,
    selector: 'tests-runner-logs',
    templateUrl: 'tests-runner-logs.component.html',
    styleUrls: ["tests-runner-logs.component.scss"],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TestsRunnerLogsComponent implements AfterViewInit, OnInit, OnDestroy {

    showLastLogWhenChanged = true;

    @ViewChild('logsContainer') logsContainer: ElementRef;
    @ViewChild('logsFooter') scrollFooter: ElementRef;

    logsToDisplay: Array<TestsRunnerLogModel> = [];
    selectedRunnerTreeNode: RunnerTreeNodeModel;
    shouldWrapLogs: boolean;
    minLogLevelToShow: LogLevel = LogLevel.INFO;

    private lastLogsCount: number = 0;
    private lastLogScrollOffset: number = 0;

    LogLineTypeEnum = LogLineTypeEnum;
    LogLevel = LogLevel;

    logAddedEventEmitterSubscription:any;
    emptyLogsEventEmitterSubscription:any;
    selectedRunnerTreeNodeSubscription: Subscription;

    constructor(private cd: ChangeDetectorRef,
                private testRunnerService: TestsRunnerService,
                private testsRunnerLogsService: TestsRunnerLogsService) {
    }

    ngOnInit(): void {

        this.lastLogsCount = this.testsRunnerLogsService.logsByEventKey.size;

        this.logsToDisplay.length = 0;
        this.testsRunnerLogsService.logsByEventKey.forEach((logs: Array<TestsRunnerLogModel>, key: string) => {
            for (const log of logs) {
                this.logsToDisplay.push(log);
            }
        });

        this.selectedRunnerTreeNodeSubscription = this.testRunnerService.selectedRunnerTreeNodeObserver.subscribe((selectedNode: RunnerTreeNodeModel) => {
            this.onRunnerTreeNodeSelected(selectedNode);
            this.refresh();
        });

        this.logAddedEventEmitterSubscription = this.testsRunnerLogsService.logAddedEventEmitter.asObservable()
            .pipe(bufferTime(500))
            .subscribe((logs:TestsRunnerLogModel[]) => {
                for (const log of logs) {
                    if(this.selectedRunnerTreeNode == null || this.isLogBelongingToRunnerTreeNode(log.eventKey, this.selectedRunnerTreeNode)) {
                        this.logsToDisplay.push(log);
                    }
                }
                this.refresh();
            });


        this.emptyLogsEventEmitterSubscription = this.testsRunnerLogsService.emptyLogsEventEmitter.subscribe(
            event => {
                this.logsToDisplay.length = 0;
                this.selectedRunnerTreeNode = null;
                this.refresh();
            }
        );

        this.testRunnerService.runnerVisibleEventEmitter.subscribe(() => {
            this.refresh()
        })
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

    ngAfterViewInit(): void {

        let changes = new MutationObserver((mutations: MutationRecord[]) => {
            if (!this.selectedRunnerTreeNode && this.showLastLogWhenChanged) {
                this.lastLogsCount = this.testsRunnerLogsService.logsByEventKey.size;
                this.scrollFooter.nativeElement.scrollIntoView();
            }
        });

        changes.observe(this.logsContainer.nativeElement, {
            attributes: false,
            childList: true,
            characterData: false
        });
    }

    onLogScrollEvent(event) {
        var container = event.target;
        var contHeight = container.clientHeight;
        var scrollTop = container.scrollTop;
        var scrollHeight  = container.scrollHeight ;

        if (this.showLastLogWhenChanged && this.lastLogScrollOffset > scrollTop) {
            this.showLastLogWhenChanged = false;
        }

        if (!this.showLastLogWhenChanged && contHeight == scrollHeight - scrollTop) {
            this.showLastLogWhenChanged = true;
        }
        this.lastLogScrollOffset = scrollTop;
    }

    trackByLogKey(index: number, log: TestsRunnerLogModel): string {
        return log.eventKey;
    };

    private onRunnerTreeNodeSelected(runnerTreeNode: RunnerTreeNodeModel): void {
        this.selectedRunnerTreeNode = runnerTreeNode;
        this.refreshLogs();
    }

    private refreshLogs() {
        this.logsToDisplay.length = 0;
        this.testsRunnerLogsService.logsByEventKey.forEach((logs: Array<TestsRunnerLogModel>, key: string) => {
            if (this.selectedRunnerTreeNode && !this.isLogBelongingToRunnerTreeNode(key, this.selectedRunnerTreeNode)) {
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

    private isLogBelongingToRunnerTreeNode(logEventKeyAsString: string, runnerTreeNode: RunnerTreeNodeModel): boolean {
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
        this.refreshLogs();
        this.refresh();
    }

    refresh() {
        if (!this.cd['destroyed']) {
            this.cd.detectChanges();
        }
    }
}
