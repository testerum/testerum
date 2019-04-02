import {EventEmitter, Injectable} from "@angular/core";
import {$WebSocket, WebSocketConfig} from "angular2-websocket/angular2-websocket";

@Injectable()
export class ProjectReloadWsService {

    private static URL = "/rest/project-reloaded-ws";

    readonly projectReloadedEventEmitter: EventEmitter</*projectRootDir: */string> = new EventEmitter<string>();

    private webSocket:$WebSocket = null;

    start() {
        this.connect();
    }

    private connect() {
        if (this.webSocket) {
            this.webSocket.close(true);
            this.webSocket = null;
        }
        let webSocketUrl = ProjectReloadWsService.getWebSocketUrl();
        this.webSocket = new $WebSocket(webSocketUrl, null, { reconnectIfNotNormalClose: true } as WebSocketConfig);

        this.webSocket.onError((error) => {
            console.log(`project reload WebSocket dataStream: error: ${error.message}`, error);
            this.webSocket.close(true);
            this.webSocket = null;
        });

        this.webSocket.onMessage((message) => {
            this.handleServerMessage(message);
        });
    }

    private handleServerMessage(message: MessageEvent) {
        this.projectReloadedEventEmitter.emit(message.data);
    }

    private static getWebSocketUrl(): string {
        let loc = window.location;

        let result: string = "";
        if (loc.protocol === "https:") {
            result = "wss:";
        } else {
            result = "ws:";
        }
        result += "//" + loc.host;
        result += ProjectReloadWsService.URL;

        return result;
    }

}
