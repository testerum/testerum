import { Injectable } from '@angular/core';
import { TestModel } from "../../../model/test/test.model";
import { RunnerEvent, RunnerEventMarshaller } from "../../../model/test/event/runner.event";
import { $WebSocket, WebSocketConfig, WebSocketSendMode } from "angular2-websocket/angular2-websocket";
import { HttpClient } from "@angular/common/http";
import { Observable, Subject } from "rxjs";
import { TestExecutionResponse } from "../../../model/runner/tree/test-execution-response.model";

@Injectable()
export class TestsRunnerService {

    private static URLS = {
        REST_CREATE_TEST_EXECUTION: "/rest/tests/executions",
        REST_STOP_TEST_EXECUTION  : "/rest/tests/executions",
        WEB_SOCKET                : "ws://localhost:8080/rest/tests-ws",
    };

    public isTestRunnerVisible:boolean = false;

    private webSocket:$WebSocket = null;
    private executionId: number = null;

    private startTestExecutionSubject: Subject<TestModel[]> = new Subject<TestModel[]>();
    public readonly startTestExecutionObservable: Observable<TestModel[]> = this.startTestExecutionSubject;

    private runnerEventSubject: Subject<RunnerEvent> = new Subject<RunnerEvent>();
    public readonly runnerEventObservable: Observable<RunnerEvent> = this.runnerEventSubject;

    constructor(private http: HttpClient) {}

    runTests(testModels: TestModel[]) {
        this.isTestRunnerVisible = true;

        this.startTestExecutionSubject.next(testModels);

        const testModelPaths = testModels.map(testModel => testModel.path.toString());
        this.http.post(TestsRunnerService.URLS.REST_CREATE_TEST_EXECUTION, testModelPaths)
            .subscribe((testExecutionResponse: TestExecutionResponse) => {
                this.startExecution(testExecutionResponse);
            });
    }

    private startExecution(testExecutionResponse: TestExecutionResponse) {
        // todo: also use testExecutionResponse.runnerRootNode
        console.log("testExecutionResponse.runnerRootNode=", testExecutionResponse.runnerRootNode);

        this.executionId = testExecutionResponse.executionId;

        let payload = `EXECUTE-TESTS:${this.executionId}`;

        this.connectWebSocket();
        this.sendMessage(payload);
    }

    private connectWebSocket() {
        this.webSocket = new $WebSocket(TestsRunnerService.URLS.WEB_SOCKET, null, { reconnectIfNotNormalClose: true } as WebSocketConfig);

        this.webSocket.onError((error) => {
            console.log(`runner WebSocket dataStream: error: ${error.message}`, error);
            this.webSocket = null;
        });

        this.webSocket.onMessage((message) => {
            this.handleServerMessage(message);
        });
    }

    private handleServerMessage(message: MessageEvent) {
        let runnerEventAsJson = JSON.parse(message.data);

        let eventTypeAsString:string = runnerEventAsJson["@type"];
        let runnerEvent:RunnerEvent = RunnerEventMarshaller.deserializeRunnerEvent(runnerEventAsJson);

        this.runnerEventSubject.next(runnerEvent);
    }

    private sendMessage(payload: string) {
        if (this.webSocket == null) {
            console.warn(`attempting to send message without a WebSocket connection`);
            return;
        }

        this.webSocket.send(payload, WebSocketSendMode.Direct);
    }

    public stopExecution() {
        if (this.executionId === null) {
            console.warn("trying to stop an execution without an execution id");
            return;
        }

        this.http.delete(`${TestsRunnerService.URLS.REST_STOP_TEST_EXECUTION}/${this.executionId}`)
            .subscribe();
    }

    private static serializeTestModels(testModels: TestModel[]): string {
        let result = "[";
        testModels.forEach((testModel, index) => {
            result += testModel.serialize();
            if(index < testModels.length - 1) {
                result += ","
            }
        });

        return result + "]";
    }

}
