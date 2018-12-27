import {RunnerEvent} from "./runner.event";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";
import {ExceptionDetail} from "./fields/exception-detail.model";

export class TextLogEvent implements RunnerEvent, Serializable<TextLogEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.LOG_EVENT;

    message: string;
    exceptionDetail: ExceptionDetail | null;

    deserialize(input: Object): TextLogEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.message = input["message"];
        if (input["exceptionDetail"]) {
            this.exceptionDetail = new ExceptionDetail().deserialize(input["exceptionDetail"]);
        }

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
