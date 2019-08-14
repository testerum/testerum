import {RunnerEvent} from "./runner.event";
import {Path} from "../../infrastructure/path/path.model";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class ParametrizedTestStartEvent implements RunnerEvent, Serializable<ParametrizedTestStartEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.PARAMETRIZED_TEST_START_EVENT;

    testName: string;
    testFilePath: Path;
    tags: Array<string> = [];

    deserialize(input: Object): ParametrizedTestStartEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.testName = input["testName"];
        this.testFilePath = Path.deserialize(input["testFilePath"]);
        this.tags = input['tags'] || [];

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
