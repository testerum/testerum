import {RunnerEvent} from "./runner.event";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class RunnerStoppedEvent implements RunnerEvent, Serializable<RunnerStoppedEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.RUNNER_STOPPED_EVENT;

    deserialize(input: Object): RunnerStoppedEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
