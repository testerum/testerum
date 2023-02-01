import {RunnerEvent} from "./runner.event";
import {ExecutionStatusEnum} from "./enums/execution-status.enum";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class FeatureEndEvent implements RunnerEvent, Serializable<FeatureEndEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.FEATURE_END_EVENT;

    featureName: string;

    status: ExecutionStatusEnum;
    durationMillis: number;

    deserialize(input: Object): FeatureEndEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);
        this.featureName = input["featureName"];

        let statusAsString:string = input["status"];
        this.status = ExecutionStatusEnum[statusAsString];

        this.durationMillis = input["durationMillis"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
