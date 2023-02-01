import {RunnerEvent} from "./runner.event";
import {ExecutionStatusEnum} from "./enums/execution-status.enum";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class SuiteEndEvent implements RunnerEvent, Serializable<SuiteEndEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.TEST_SUITE_END_EVENT;

    status: ExecutionStatusEnum;
    durationMillis: number;

    deserialize(input: Object): SuiteEndEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);

        let statusAsString:string = input["status"];
        this.status = ExecutionStatusEnum[statusAsString];

        this.durationMillis = input["durationMillis"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
