
import {RunnerEvent} from "./runner.event";
import {StepCall} from "../../step-call.model";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";

export class StepStartEvent implements RunnerEvent, Serializable<StepStartEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.STEP_START_EVENT;

    stepCall: StepCall;

    deserialize(input: Object): StepStartEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.stepCall = new StepCall().deserialize(input["stepCall"]);
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
