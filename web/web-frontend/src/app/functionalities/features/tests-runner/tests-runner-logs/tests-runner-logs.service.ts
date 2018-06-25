import {EventEmitter, Injectable, ViewChild, ViewContainerRef} from '@angular/core';
import {TestWebSocketService} from "../../../../service/test-web-socket.service";
import {WebSocketEventListener} from "../../../../service/event/web-socket-event.listener";
import {RunnerEvent} from "../../../../model/test/event/runner.event";
import {TestsRunnerLogModel} from "./model/tests-runner-log.model";
import {StepEndEvent} from "../../../../model/test/event/step-end.event";
import {StepStartEvent} from "../../../../model/test/event/step-start.event";
import {TestEndEvent} from "../../../../model/test/event/test-end.event";
import {TestStartEvent} from "../../../../model/test/event/test-start.event";
import {SuiteEndEvent} from "../../../../model/test/event/suite-end.event";
import {SuiteStartEvent} from "../../../../model/test/event/suite-start.event";
import {ExecutionStatusEnum} from "../../../../model/test/event/enums/execution-status.enum";
import {RunnerTreeNodeSelectedListener} from "../tests-runner-tree/event/runner-tree-node-selected.listener";
import {RunnerTreeNodeModel} from "../tests-runner-tree/model/runner-tree-node.model";
import {RunnerTreeService} from "../tests-runner-tree/runner-tree.service";
import {StepCall} from "../../../../model/step-call.model";
import {RunnerErrorEvent} from "../../../../model/test/event/runner-error.event";
import {TextLogEvent} from "../../../../model/test/event/text-log.event";

@Injectable()
export class TestsRunnerLogsService implements WebSocketEventListener {

    logs: Array<TestsRunnerLogModel> = [];
    logAddedEventEmitter: EventEmitter<TestsRunnerLogModel> = new EventEmitter<TestsRunnerLogModel>();
    emptyLogsEventEmitter: EventEmitter<void> = new EventEmitter<void>();

    constructor(private testWebSocketService: TestWebSocketService) {
    }

    initialize() {
        this.testWebSocketService.removeWebSocketEventListener(this);
        this.testWebSocketService.addWebSocketEventListener(this);

        this.logs.length = 0;
        this.emptyLogsEventEmitter.emit();
    }

    onWebSocketMessage(runnerEvent: RunnerEvent): void {
        let logEvent: TestsRunnerLogModel = TestsRunnerLogsService.transformEvent(runnerEvent);

        this.logs.push(logEvent);
        this.logAddedEventEmitter.emit(logEvent);
    }

    public static transformEvent(runnerEvent: RunnerEvent): TestsRunnerLogModel {

        let log = new TestsRunnerLogModel();
        log.time = runnerEvent.time;
        log.eventType = runnerEvent.eventType;
        log.eventKey = runnerEvent.eventKey;

        if (runnerEvent instanceof SuiteStartEvent) {
            log.addLogLine("Executing test suite")
        }
        if (runnerEvent instanceof SuiteEndEvent) {
            log.addLogLine("Finished executing test suite")
        }
        if (runnerEvent instanceof TestStartEvent) {
            log.addLogLine(`Executing test "${runnerEvent.testName}"`)
        }
        if (runnerEvent instanceof TestEndEvent) {

            log.addLogLine(`Finished executing test "${runnerEvent.testName}"`);

            //TODO: print exception details
            if (runnerEvent.exceptionDetail) {
                let errorMessage = runnerEvent.exceptionDetailAsString;
                log.addExceptionLine("errorMessage:\n\t" + errorMessage);
            }
        }
        if (runnerEvent instanceof StepStartEvent) {

            let stepCall:StepCall = runnerEvent.stepCall;
            log.addLogLine(`Executing step "${stepCall.getTextWithParamValues(null)}"`)
        }
        if (runnerEvent instanceof StepEndEvent) {
            let stepCall: StepCall = runnerEvent.stepCall;

            log.addLogLine(`Finished executing step "${stepCall.getTextWithParamValues(null)}"`);
            log.addLogLine(`status: ${ExecutionStatusEnum[runnerEvent.status]}`);

            //TODO: print exception details
            if (runnerEvent.exceptionDetail) {
                let errorMessage = runnerEvent.exceptionDetailAsString;
                log.addExceptionLine("errorMessage:\n\t" + errorMessage);
            }
        }
        if (runnerEvent instanceof RunnerErrorEvent) {
            log.addExceptionLine("Exception: " + runnerEvent.errorMessage)
        }
        if (runnerEvent instanceof TextLogEvent) {
            log.addLogLine(runnerEvent.message)
        }
        return log;
    }

}
