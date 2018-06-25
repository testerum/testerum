import {$WebSocket, WebSocketSendMode, WebSocketConfig} from 'angular2-websocket/angular2-websocket'
import {Injectable} from "@angular/core";
import {TestModel} from "../model/test/test.model";
import {Observable} from "rxjs/Observable";
import {WebSocketEventListener} from "./event/web-socket-event.listener";
import {IdUtils} from "../utils/id.util";
import {RunnerEventTypeEnum} from "../model/test/event/enums/runner-event-type.enum";
import {RunnerEvent} from "../model/test/event/runner.event";
import {SuiteEndEvent} from "../model/test/event/suite-end.event";
import {SuiteStartEvent} from "../model/test/event/suite-start.event";
import {TestStartEvent} from "../model/test/event/test-start.event";
import {TestEndEvent} from "../model/test/event/test-end.event";
import {StepStartEvent} from "../model/test/event/step-start.event";
import {StepEndEvent} from "../model/test/event/step-end.event";
import {RunnerErrorEvent} from "../model/test/event/runner-error.event";
import {TextLogEvent} from "../model/test/event/text-log.event";
import {ArrayUtil} from "../utils/array.util";

@Injectable()
export class TestWebSocketService {

    private TEST_WEB_SOCKET_URL = "ws://localhost:8080/rest/tests-ws";

    private webSocket:$WebSocket = null;
    private webSocketConnectionId:string = null;

    private runnerEventListener:Array<WebSocketEventListener> = [];

    addWebSocketEventListener(messageListeners:WebSocketEventListener) {
        this.runnerEventListener.push(messageListeners);
    }

    removeWebSocketEventListener(messageListener:WebSocketEventListener) {
        let index: number = this.runnerEventListener.indexOf(messageListener);
        if (index !== -1) {
            this.runnerEventListener.splice(index, 1);
        }
    }

    runTests(testModels:Array<TestModel>){
        let payload: string = this.serializeTestModels(testModels);

        this.connect();
        this.webSocketConnectionId = IdUtils.getTemporaryId();

        payload = "EXECUTE_TEST-"+this.webSocketConnectionId+";"+payload;
        this.sendMessage(payload)
    }

    private serializeTestModels(testModels: Array<TestModel>) {
        let result = "[";
        testModels.forEach((testModel, index) => {
            result += testModel.serialize();
            if(index < testModels.length - 1) {
                result += ","
            }
        });

        return result + "]";
    }

    private connect() {
        this.webSocket = new $WebSocket(this.TEST_WEB_SOCKET_URL, null, { reconnectIfNotNormalClose: true } as WebSocketConfig);
        this.webSocket.getDataStream().subscribe(
            res => {
                this.handleMessage(res);

            },
            function(e) { console.log('Error: ' + e.message); },
            function() { console.log('Completed'); }
        );
    }

    private close() {
        this.sendMessage("CLOSE-"+this.webSocketConnectionId);
    }

    private sendMessage(payload: string) {
        if(this.webSocket != null) {
            this.webSocket.send(payload).subscribe(
                (msg:any)=> {
                    console.log("webSocket-next", msg.data);
                },
                (msg:any)=> {
                    console.log("webSocket-error", msg);
                },
                ()=> {
                    console.log("webSocket-complete");
                }
            );
        }
    }

    private handleMessage(res: MessageEvent) {
        let runnerEventAsJson = JSON.parse(res.data);

        console.log("runnerEventReceived", runnerEventAsJson);

        let eventTypeAsString:string = runnerEventAsJson["@type"];
        if(RunnerEventTypeEnum.TEST_SUITE_END_EVENT == RunnerEventTypeEnum[eventTypeAsString]) {
            this.close();
        }

        let runnerEvent:RunnerEvent = TestWebSocketService.deserializeRunnerEvent(runnerEventAsJson);

        for (let runnerEventListener of this.runnerEventListener) {
            runnerEventListener.onWebSocketMessage(runnerEvent)
        }
    }

    public static deserializeRunnerEvent(runnerEventAsJson: object):RunnerEvent {
        switch (runnerEventAsJson["@type"]) {
            case "TEST_SUITE_START_EVENT": return new SuiteStartEvent().deserialize(runnerEventAsJson);
            case "TEST_SUITE_END_EVENT": return new SuiteEndEvent().deserialize(runnerEventAsJson);
            case "TEST_START_EVENT": return new TestStartEvent().deserialize(runnerEventAsJson);
            case "TEST_END_EVENT": return new TestEndEvent().deserialize(runnerEventAsJson);
            case "STEP_START_EVENT": return new StepStartEvent().deserialize(runnerEventAsJson);
            case "STEP_END_EVENT": return new StepEndEvent().deserialize(runnerEventAsJson);
            case "ERROR_EVENT": return new RunnerErrorEvent().deserialize(runnerEventAsJson);
            case "LOG_EVENT": return new TextLogEvent().deserialize(runnerEventAsJson);
        }

        return null;
    }
}
