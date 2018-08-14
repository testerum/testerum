import {EventEmitter, Injectable} from '@angular/core';
import { RunnerEvent, RunnerEventMarshaller } from "../../../model/test/event/runner.event";
import { $WebSocket, WebSocketConfig, WebSocketSendMode } from "angular2-websocket/angular2-websocket";
import { HttpClient } from "@angular/common/http";
import { TestExecutionResponse } from "../../../model/runner/tree/test-execution-response.model";
import { map } from "rxjs/operators";
import {RunnerRootNode} from "../../../model/runner/tree/runner-root-node.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {RunnerTreeNodeModel} from "./tests-runner-tree/model/runner-tree-node.model";
import {SuiteEndEvent} from "../../../model/test/event/suite-end.event";
import {RunnerErrorEvent} from "../../../model/test/event/runner-error.event";
import {RunnerTreeFilterModel} from "./tests-runner-tree/model/filter/runner-tree-filter.model";

@Injectable()
export class TestsRunnerService {

    private static URLS = {
        REST_CREATE_TEST_EXECUTION: "/rest/tests/executions",
        REST_STOP_TEST_EXECUTION  : "/rest/tests/executions",
        WEB_SOCKET                : "ws://localhost:8080/rest/tests-ws",
    };

    public isTestRunnerVisible:boolean = false;
    public areTestRunning: boolean = false;
    public lastRunPaths: Path[];

    private webSocket:$WebSocket = null;
    private executionId: number = null;

    selectedRunnerTreeNode: RunnerTreeNodeModel;
    readonly selectedRunnerTreeNodeObserver: EventEmitter<RunnerTreeNodeModel> = new EventEmitter<RunnerTreeNodeModel>();

    readonly startTestExecutionObservable: EventEmitter<RunnerRootNode> = new EventEmitter<RunnerRootNode>();
    readonly runnerEventObservable: EventEmitter<RunnerEvent> = new EventEmitter<RunnerEvent>();
    readonly showTestFoldersEventObservable: EventEmitter<boolean> = new EventEmitter<boolean>();
    readonly treeFilterObservable: EventEmitter<RunnerTreeFilterModel> = new EventEmitter<RunnerTreeFilterModel>();

    constructor(private http: HttpClient) {}

    runTests(pathsToExecute: Path[]) {
        this.lastRunPaths = pathsToExecute;
        this.isTestRunnerVisible = true;

        let pathsAsString = pathsToExecute.map(it => {return it.toString()});
        this.http.post(TestsRunnerService.URLS.REST_CREATE_TEST_EXECUTION, pathsAsString)
            .pipe(map(json => new TestExecutionResponse().deserialize(json)))
            .subscribe((testExecutionResponse: TestExecutionResponse) => {
                this.startExecution(testExecutionResponse);
            });
    }

    reRunTests() {
        this.runTests(this.lastRunPaths)
    }

    private startExecution(testExecutionResponse: TestExecutionResponse) {
        this.areTestRunning = true;
        this.executionId = testExecutionResponse.executionId;

        this.startTestExecutionObservable.emit(testExecutionResponse.runnerRootNode);

        let payload = `EXECUTE-TESTS:${this.executionId}`;

        this.connectWebSocket();
        this.sendMessage(payload);
    }

    private connectWebSocket() {
        this.webSocket = new $WebSocket(TestsRunnerService.URLS.WEB_SOCKET, null, { reconnectIfNotNormalClose: true } as WebSocketConfig);

        this.webSocket.onError((error) => {
            console.log(`runner WebSocket dataStream: error: ${error.message}`, error);
            this.webSocket.close(true);
            this.webSocket = null;
        });

        this.webSocket.onMessage((message) => {
            this.handleServerMessage(message);
        });

        this.webSocket.onClose( (event) => {
            // let RunnerErrorEvent
            // handleServerMessage(message: MessageEvent)
        })
    }

    private handleServerMessage(message: MessageEvent) {
        let runnerEventAsJson = JSON.parse(message.data);

        let runnerEvent:RunnerEvent = RunnerEventMarshaller.deserializeRunnerEvent(runnerEventAsJson);

        if (runnerEvent instanceof SuiteEndEvent ||
            runnerEvent instanceof RunnerErrorEvent) {
            this.areTestRunning = false;
        }

        console.log(runnerEvent);
        this.runnerEventObservable.emit(runnerEvent);
    }

    private sendMessage(payload: string) {
        if (this.webSocket == null) {
            console.warn(`attempting to send message without a WebSocket connection`);
            return;
        }

        this.webSocket.send(payload, WebSocketSendMode.Direct);
    }

    public stopExecution() {
        this.areTestRunning = false;
        if (this.executionId === null) {
            console.warn("trying to stop an execution without an execution id");
            return;
        }

        this.http.delete(`${TestsRunnerService.URLS.REST_STOP_TEST_EXECUTION}/${this.executionId}`)
            .subscribe();
    }

    public setSelectedNode(selectedRunnerTreeNode: RunnerTreeNodeModel) {
        this.selectedRunnerTreeNode = selectedRunnerTreeNode;
        this.selectedRunnerTreeNodeObserver.emit(selectedRunnerTreeNode);
    }
}
