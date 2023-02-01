import {RunnerEvent} from "./runner.event";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class FeatureStartEvent implements RunnerEvent, Serializable<FeatureStartEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.FEATURE_START_EVENT;

    featureName: string;
    tags: Array<string> = [];

    deserialize(input: Object): FeatureStartEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);
        this.featureName = input["featureName"];
        this.tags = input['tags'] || [];

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
