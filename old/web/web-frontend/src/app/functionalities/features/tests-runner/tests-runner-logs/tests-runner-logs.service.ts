import {EventEmitter, Injectable} from '@angular/core';
import {RunnerEvent} from "../../../../model/test/event/runner.event";
import {TestsRunnerLogModel} from "./model/tests-runner-log.model";
import {RunnerErrorEvent} from "../../../../model/test/event/runner-error.event";
import {TextLogEvent} from "../../../../model/test/event/text-log.event";
import {TestsRunnerService} from "../tests-runner.service";
import {Subscription} from "rxjs";
import {LogLevel} from "../../../../model/test/event/enums/log-level.enum";

@Injectable()
export class TestsRunnerLogsService {

    logsByEventKey: Map<string, Array<TestsRunnerLogModel>> = new Map<string, Array<TestsRunnerLogModel>>();
    logAddedEventEmitter: EventEmitter<TestsRunnerLogModel> = new EventEmitter<TestsRunnerLogModel>();
    emptyLogsEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    private runnerEventSubscription: Subscription = null;

    constructor(private testsRunnerService: TestsRunnerService) {
        testsRunnerService.startTestExecutionObservable.subscribe(() => {
            this.onStartTestExecution();
        })
    }

    private onStartTestExecution() {
        if (this.runnerEventSubscription) {
            this.runnerEventSubscription.unsubscribe();
        }
        this.runnerEventSubscription = this.testsRunnerService.runnerEventObservable.subscribe((runnerEvent) => {
            this.onRunnerEvent(runnerEvent);
        });

        this.logsByEventKey.clear();
        this.emptyLogsEventEmitter.emit();
    }

    private onRunnerEvent(runnerEvent: RunnerEvent): void {
        let logEvent: TestsRunnerLogModel = TestsRunnerLogsService.transformEvent(runnerEvent);

        let logsValue: Array<TestsRunnerLogModel> = this.logsByEventKey.get(logEvent.eventKey);
        if (!logsValue) {
            logsValue = [];
            this.logsByEventKey.set(logEvent.eventKey, logsValue)
        }
        logsValue.push(logEvent);

        this.logAddedEventEmitter.emit(logEvent);
    }

    public static transformEvent(runnerEvent: RunnerEvent): TestsRunnerLogModel {

        let log = new TestsRunnerLogModel();
        log.time = runnerEvent.time;
        log.eventType = runnerEvent.eventType;
        log.eventKey = runnerEvent.eventKey;

        if (runnerEvent instanceof RunnerErrorEvent) {
            log.addException("Exception: " + runnerEvent.errorMessage);
            log.logLevel = LogLevel.ERROR;
        }
        if (runnerEvent instanceof TextLogEvent) {
            log.addLogLine(runnerEvent.message);
            log.logLevel = runnerEvent.logLevel;
            if (runnerEvent.exceptionDetail != null) {
                log.addException(
                    runnerEvent.exceptionDetail.message,
                    runnerEvent.exceptionDetail.asDetailedString
                );
            }
        }

        return log;
    }
}
