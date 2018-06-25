
import {RunnerEvent} from "../../model/test/event/runner.event";

export interface WebSocketEventListener {
    onWebSocketMessage(runnerEvent:RunnerEvent): void;
}
