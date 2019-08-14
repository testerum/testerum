import {RunnerEvent} from "./runner.event";
import {Path} from "../../infrastructure/path/path.model";
import {EventKey} from "./fields/event-key.model";
import {RunnerEventTypeEnum} from "./enums/runner-event-type.enum";
import {Serializable} from "../../infrastructure/serializable.model";
import {Scenario} from "../scenario/scenario.model";
import {StringUtils} from "../../../utils/string-utils.util";
import {ObjectUtil} from "../../../utils/object.util";

export class ScenarioStartEvent implements RunnerEvent, Serializable<ScenarioStartEvent> {
    time: Date;
    eventKey: EventKey;
    eventType: RunnerEventTypeEnum = RunnerEventTypeEnum.SCENARIO_START_EVENT;

    testName: string;
    testFilePath: Path;
    scenario: Scenario;
    scenarioIndex: number;
    tags: Array<string> = [];

    deserialize(input: Object): ScenarioStartEvent {
        this.eventKey = new EventKey().deserialize(input["eventKey"]);
        this.time = new Date(input["time"]);
        this.testName = input["testName"];
        this.testFilePath = Path.deserialize(input["testFilePath"]);
        this.scenario = new Scenario().deserialize(input);
        this.scenarioIndex = ObjectUtil.getAsNumber(input["scenarioIndex"]);
        this.tags = input['tags'] || [];

        return this;
    }

    serialize(): string {
        return undefined;
    }
}
