import {RunnerEvent} from "./runner.event";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";
import {ExceptionDetail} from "./fields/exception-detail.model";
import {LogLevel} from "./enums/log-level.enum";

export class TextLogEvent implements RunnerEvent, Serializable<TextLogEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.LOG_EVENT;

    logLevel: LogLevel;
    message: string;
    exceptionDetail: ExceptionDetail | null;

    deserialize(input: Object): TextLogEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);
        this.message = input["message"];
        if (input["exceptionDetail"]) {
            this.exceptionDetail = new ExceptionDetail().deserialize(input["exceptionDetail"]);
        }
        this.logLevel = LogLevel[""+input["logLevel"]];


        return this;
    }

    serialize(): string {
        return undefined;
    }

    getMessageWithException(): string {
        let result = "";

        result += this.message;

        if (this.exceptionDetail != null) {
            result += "; exception:\n";
            result += this.exceptionDetail.asDetailedString;
        }

        return result;
    }

}
