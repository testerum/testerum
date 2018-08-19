import {RunnerEvent} from "./runner.event";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class RunnerErrorEvent implements RunnerEvent, Serializable<RunnerErrorEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.ERROR_EVENT;

    errorMessage: string;

    deserialize(input: Object): RunnerErrorEvent {
        this.time = new Date(input["time"]);
        this.errorMessage = input["errorMessage"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
