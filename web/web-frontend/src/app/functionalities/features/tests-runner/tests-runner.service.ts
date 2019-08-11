import {EventEmitter, Injectable} from '@angular/core';
import {RunnerEvent} from "../../../model/test/event/runner.event";
import {$WebSocket, WebSocketConfig, WebSocketSendMode} from "angular2-websocket/angular2-websocket";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {TestExecutionResponse} from "../../../model/runner/tree/test-execution-response.model";
import {map} from "rxjs/operators";
import {RunnerRootNode} from "../../../model/runner/tree/runner-root-node.model";
import {Path} from "../../../model/infrastructure/path/path.model";
import {RunnerTreeNodeModel} from "./tests-runner-tree/model/runner-tree-node.model";
import {SuiteEndEvent} from "../../../model/test/event/suite-end.event";
import {RunnerErrorEvent} from "../../../model/test/event/runner-error.event";
import {RunnerTreeFilterModel} from "./tests-runner-tree/model/filter/runner-tree-filter.model";
import {RunnerEventMarshaller} from '../../../model/test/event/marshaller/runner-event-marshaller';
import {ContextService} from "../../../service/context.service";
import {Project} from "../../../model/home/project.model";
import {RunConfig} from "../../config/run-config/model/runner-config.model";
import {PathWithScenarioIndexes} from "../../config/run-config/model/path-with-scenario-indexes.model";

@Injectable()
export class TestsRunnerService {

    private static URLS = {
        REST_CREATE_TEST_EXECUTION: "/rest/tests/executions",
        REST_STOP_TEST_EXECUTION  : "/rest/tests/executions",
        WEB_SOCKET_PATH           : "/rest/tests-ws",
    };

    private selectedNode: RunnerTreeNodeModel;

    private _isRunnerVisible:boolean = false;
    readonly runnerVisibleEventEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    public areTestRunning: boolean = false;
    public lastRunConfig: RunConfig;

    private webSocket:$WebSocket = null;
    private executionId: number = null;

    readonly selectedRunnerTreeNodeObserver: EventEmitter<RunnerTreeNodeModel> = new EventEmitter<RunnerTreeNodeModel>();

    readonly startTestExecutionObservable: EventEmitter<RunnerRootNode> = new EventEmitter<RunnerRootNode>();
    readonly runnerEventObservable: EventEmitter<RunnerEvent> = new EventEmitter<RunnerEvent>();
    readonly showTestFoldersEventObservable: EventEmitter<boolean> = new EventEmitter<boolean>();
    readonly treeFilterObservable: EventEmitter<RunnerTreeFilterModel> = new EventEmitter<RunnerTreeFilterModel>();

    constructor(private http: HttpClient,
                private contextService: ContextService) {
        contextService.projectChangedEventEmitter.subscribe((project: Project) => {
            this.setRunnerVisibility(false);
        });
    }

    runTests(pathsToExecute: Path[]) {
        const runConfig = new RunConfig();
        runConfig.name = "temp run config";
        runConfig.pathsToInclude = pathsToExecute.map(pathToExecute => {
            const pathWithScenarioIndexes = new PathWithScenarioIndexes();

            pathWithScenarioIndexes.path = pathToExecute;
            pathWithScenarioIndexes.scenarioIndexes = [];

            return pathWithScenarioIndexes
        });

        this.runRunConfig(runConfig);
    }

    runRunConfig(runConfig: RunConfig) {
        this.lastRunConfig = runConfig;
        this.setRunnerVisibility(true);

        const bodyAsString = runConfig.serialize();

        const headers = new HttpHeaders({
            "Content-Type": "application/json",
        });
        const options = { headers: headers };

        this.http.post(TestsRunnerService.URLS.REST_CREATE_TEST_EXECUTION, bodyAsString, options)
            .pipe(map(json => new TestExecutionResponse().deserialize(json)))
            .subscribe((testExecutionResponse: TestExecutionResponse) => {
                this.startExecution(testExecutionResponse);
            });
    }

    reRunTests() {
        this.runRunConfig(this.lastRunConfig)
    }

    isRunnerVisible(): boolean {
        return this._isRunnerVisible;
    }

    setRunnerVisibility(isVisible: boolean) {
        this._isRunnerVisible = isVisible;
        this.runnerVisibleEventEmitter.emit(isVisible);
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
        if (this.webSocket) {
            this.webSocket.close(true);
            this.webSocket = null;
        }
        let webSocketUrl = this.getWebSocketUrl();
        this.webSocket = new $WebSocket(webSocketUrl, null, { reconnectIfNotNormalClose: true } as WebSocketConfig);

        this.webSocket.onError((error) => {
            console.log(`runner WebSocket dataStream: error: ${error.message}`, error);
            this.webSocket.close(true);
            this.webSocket = null;
        });

        this.webSocket.onMessage((message) => {
            this.handleServerMessage(message);
        });
    }

    private getWebSocketUrl(): string {
        let loc = window.location;
        let wsHost: string = "";
        if (loc.protocol === "https:") {
            wsHost = "wss:";
        } else {
            wsHost = "ws:";
        }
        wsHost += "//" + loc.host;
        wsHost += TestsRunnerService.URLS.WEB_SOCKET_PATH;
        return wsHost;
    }

    private handleServerMessage(message: MessageEvent) {
        let runnerEventAsJson = JSON.parse(message.data);

        let runnerEvent:RunnerEvent = RunnerEventMarshaller.deserializeRunnerEvent(runnerEventAsJson);

        if (runnerEvent instanceof SuiteEndEvent ||
            runnerEvent instanceof RunnerErrorEvent) {
            this.areTestRunning = false;
        }

        // console.log(runnerEvent);
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
        this.selectedNode = selectedRunnerTreeNode;
        this.selectedRunnerTreeNodeObserver.emit(selectedRunnerTreeNode);
    }

    public getSelectedNode(): RunnerTreeNodeModel {
        return this.selectedNode;
    }
}
