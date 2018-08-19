import {RunnerEvent} from "./runner.event";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class RunnerStoppedEvent implements RunnerEvent, Serializable<RunnerStoppedEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.RUNNER_STOPPED_EVENT;

    deserialize(input: Object): RunnerStoppedEvent {
        this.time = new Date(input["time"]);

        return this;
    }

    serialize(): string {
        return undefined;
    }

}
