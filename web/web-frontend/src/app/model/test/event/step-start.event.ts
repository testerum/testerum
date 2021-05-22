import {RunnerEvent} from "./runner.event";
import {StepCall} from "../../step/step-call.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class StepStartEvent implements RunnerEvent, Serializable<StepStartEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.STEP_START_EVENT;

    stepCall: StepCall;

    deserialize(input: Object): StepStartEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);
        this.stepCall = new StepCall().deserialize(input["stepCall"]);
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
