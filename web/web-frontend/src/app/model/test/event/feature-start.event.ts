import { RunnerEvent } from "./runner.event";
import { EventKey } from "./fields/event-key.model";
import { RunnerEventTypeEnum } from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class FeatureStartEvent implements RunnerEvent, Serializable<FeatureStartEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.FEATURE_START_EVENT;

    featureName: string;

    deserialize(input: Object): FeatureStartEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.featureName = input["featureName"];

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
