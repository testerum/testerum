
import {RunnerEvent} from "./runner.event";
import {Path} from "../../infrastructure/path/path.model";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";

export class SuiteStartEvent implements RunnerEvent, Serializable<SuiteStartEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.TEST_SUITE_START_EVENT;

    deserialize(input: Object): SuiteStartEvent {
        this.time = new Date(input["time"]);
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
