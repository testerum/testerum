
import {RunnerEvent} from "./runner.event";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";

export class TextLogEvent implements RunnerEvent, Serializable<TextLogEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.LOG_EVENT

    message: string;

    deserialize(input: Object): TextLogEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.message = input["message"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
