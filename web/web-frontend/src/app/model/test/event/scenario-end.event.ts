import {Path} from "../../infrastructure/path/path.model";
import {RunnerEvent} from "./runner.event";
import {ExecutionStatusEnum} from "./enums/execution-status.enum";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";
import {Scenario} from "../scenario/scenario.model";
import {ObjectUtil} from "../../../utils/object.util";

export class ScenarioEndEvent implements RunnerEvent, Serializable<ScenarioEndEvent> {
    time: Date;
    eventKey: string;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.SCENARIO_END_EVENT;

    testName: string;
    testFilePath: Path;
    scenario: Scenario;
    scenarioIndex: number;
    status: ExecutionStatusEnum;
    durationMillis: number;

    deserialize(input: Object): ScenarioEndEvent {
        this.eventKey = input["eventKey"];
        this.time = new Date(input["time"]);
        this.testName = input["testName"];
        this.testFilePath = Path.deserialize(input["testFilePath"]);
        this.scenario = new Scenario().deserialize(input);
        this.scenarioIndex = ObjectUtil.getAsNumber(input["scenarioIndex"]);
        let statusAsString:string = input["status"];
        this.status = ExecutionStatusEnum[statusAsString];

        this.durationMillis = input["durationMillis"];
        return this;
    }

    serialize(): string {
        return undefined;
    }
}
