import {RunnerEvent} from "./runner.event";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class SuiteStartEvent implements RunnerEvent, Serializable<SuiteStartEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.TEST_SUITE_START_EVENT;
    executionName: string | null;

    deserialize(input: Object): SuiteStartEvent {
        this.time = new Date(input["time"]);
        this.eventKey = new EventKey().deserialize(input["eventKey"]);

        this.executionName = input["executionName"] || null;

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
