import {RunnerEvent} from "./runner.event";
import {Path} from "../../infrastructure/path/path.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class TestStartEvent implements RunnerEvent, Serializable<TestStartEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.TEST_START_EVENT;

    testName: string;
    testFilePath: Path;
    tags: Array<string> = [];

    deserialize(input: Object): TestStartEvent {
        this.eventKey = input["eventKey"];
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
