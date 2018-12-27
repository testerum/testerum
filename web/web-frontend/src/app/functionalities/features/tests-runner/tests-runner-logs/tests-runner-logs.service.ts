import {EventEmitter, Injectable} from '@angular/core';
import {RunnerEvent} from "../../../../model/test/event/runner.event";
import {TestsRunnerLogModel} from "./model/tests-runner-log.model";
import {RunnerErrorEvent} from "../../../../model/test/event/runner-error.event";
import {TextLogEvent} from "../../../../model/test/event/text-log.event";
import {TestsRunnerService} from "../tests-runner.service";
import {Subscription} from "rxjs";

@Injectable()
export class TestsRunnerLogsService {

    logs: Array<TestsRunnerLogModel> = [];
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

        this.logs.length = 0;
        this.emptyLogsEventEmitter.emit();
    }

    private onRunnerEvent(runnerEvent: RunnerEvent): void {
        let logEvent: TestsRunnerLogModel = TestsRunnerLogsService.transformEvent(runnerEvent);

        this.logs.push(logEvent);
        this.logAddedEventEmitter.emit(logEvent);
    }

    public static transformEvent(runnerEvent: RunnerEvent): TestsRunnerLogModel {

        let log = new TestsRunnerLogModel();
        log.time = runnerEvent.time;
        log.eventType = runnerEvent.eventType;
        log.eventKey = runnerEvent.eventKey;

        if (runnerEvent instanceof RunnerErrorEvent) {
            log.addExceptionLine("Exception: " + runnerEvent.errorMessage)
        }
        if (runnerEvent instanceof TextLogEvent) {
            log.addLogLine(runnerEvent.getMessageWithException())
        }

        return log;
    }

}
