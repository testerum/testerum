import {Path} from "../../infrastructure/path/path.model";
import {RunnerEvent} from "./runner.event";
import {EventKey} from "./fields/event-key.model";
import {ExecutionStatusEnum} from "./enums/execution-status.enum";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";

export class TestEndEvent implements RunnerEvent, Serializable<TestEndEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.TEST_END_EVENT;

    testName: string;
    testFilePath: Path;
    status: ExecutionStatusEnum;
    durationMillis: number;

    deserialize(input: Object): TestEndEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.testName = input["testName"];
        this.testFilePath = Path.deserialize(input["testFilePath"]);

        let statusAsString:string = input["status"];
        this.status = ExecutionStatusEnum[statusAsString];

        this.durationMillis = input["durationMillis"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
